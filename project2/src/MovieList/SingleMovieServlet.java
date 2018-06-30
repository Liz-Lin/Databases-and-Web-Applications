package MovieList;


import Utils.MoviedbUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MovieList.SingleMovieServlet", urlPatterns = "/api/movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 3L;
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("application/json"); // Response mime type
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try
		{
			Connection dbcon = dataSource.getConnection();
			String movieId = request.getParameter("id");
			String query = "SELECT * FROM movies LEFT JOIN ratings r " +
					"on movies.id = r.movieId WHERE id=?";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, movieId);
			ResultSet resultSet = statement.executeQuery();
			JsonObject jsonObject = new JsonObject();
			if (resultSet.next())
			{
				String id = resultSet.getString("id");
				String title = resultSet.getString("title");
				String year = resultSet.getString("year");
				String director = resultSet.getString("director");
				String rating = resultSet.getString("rating");
				JsonArray stars = MoviedbUtil.getMovieStars(dbcon, id);
				JsonArray genres = MoviedbUtil.getGenres(dbcon, id);
				jsonObject.addProperty("movie_id", id);
				jsonObject.addProperty("movie_title", title);
				jsonObject.addProperty("movie_year", year);
				jsonObject.addProperty("movie_director", director);
				jsonObject.addProperty("rating", rating);
				jsonObject.add("stars", stars);
				jsonObject.add("genres", genres);
			}
			out.write(jsonObject.toString());
			// set response status to 200 (OK)
			response.setStatus(200);
			resultSet.close();
			statement.close();
			dbcon.close();
		}
		catch (Exception e)
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}
}
