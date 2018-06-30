package MovieList;

import Utils.MoviedbUtil;
import Utils.NumberUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "MovieList.MovieListServlet", urlPatterns = "/api/list")
public class MovieListServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	private PreparedStatement getStatementByGenre(HttpServletRequest request, Connection dbcon, String genreID)throws java.sql.SQLException
	{
		String query = paginationQuery(request,
				"SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director, R.rating AS rating\n" +
						"FROM genres G, genres_in_movies J,  movies M LEFT JOIN ratings R ON  M.id=R.movieId \n" +
						"WHERE J.genreId=G.id AND J.movieId=M.id  AND G.id=?");
		PreparedStatement preparedStatement = dbcon.prepareStatement(query);

		preparedStatement.setInt(1,Integer.parseInt(genreID));
		return preparedStatement;
	}

	private PreparedStatement getStatementBrowseAll(HttpServletRequest request, Connection dbcon)throws java.sql.SQLException
	{
		return dbcon.prepareStatement(
				paginationQuery(request,
						"SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director, R.rating AS rating "+
								"FROM movies M  LEFT JOIN ratings R " +
								"ON M.id=R.movieId"));
	}
	private PreparedStatement getStatementAlphabet(HttpServletRequest request, Connection dbcon, String alphabet) throws java.sql.SQLException
	{
		String query;
		PreparedStatement preparedStatement;
		if(alphabet.equals("digit")) {
			query = paginationQuery(request,
					"SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director, R.rating AS rating " +
							"FROM movies M  LEFT JOIN ratings R " +
							"ON M.id=R.movieId WHERE M.title NOT LIKE '%[^a-z0-9A-Z]%'");
			preparedStatement = dbcon.prepareStatement(query);
		}
		else {
			query = paginationQuery(request,
					"SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director, R.rating AS rating "+
							"FROM movies M  LEFT JOIN ratings R " +
							"ON M.id=R.movieId WHERE LOWER(M.title) LIKE ?");
			preparedStatement = dbcon.prepareStatement(query);
			preparedStatement.setString(1, alphabet+"%");
		}
		System.out.println(preparedStatement);
		return preparedStatement;
	}

	private PreparedStatement getStatementByTitle(HttpServletRequest request, Connection dbcon, String titleQuery) throws java.sql.SQLException
	{

		String query = "SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director,\n" +
				"       R.rating AS rating\n" +
				"FROM ratings R RIGHT JOIN movies M ON M.Id=R.movieId\n" +
				"WHERE\n" +
				"  (('%%'=?) OR M.id IN (\n" +
				"  SELECT movieId FROM stars_in_movies T INNER JOIN stars S ON T.starId=S.id AND S.name LIKE ? \n" +
				"        ) )\n" +
				"      AND M.director LIKE ? AND ( MATCH(M.title) AGAINST(? IN BOOLEAN MODE) OR  ed(M.title, ?) <= ? ) " +
				"AND (-1=? OR M.year=?) GROUP BY M.id, R.rating \n ";

		query = paginationQuery(request, query);
		PreparedStatement preparedStatement = dbcon.prepareStatement(query);

		String starName = request.getParameter("StarName");
		if (starName==null) starName="";
		starName = "%"+ starName.trim()+"%";
		preparedStatement.setString(1, starName);
		preparedStatement.setString(2, starName);
		String director = request.getParameter("Director");
		if (director==null) director="";
		preparedStatement.setString(3, "%"+director.trim()+"%");
		String trim_title = titleQuery.trim();
		String titleString = "("+ String.join( "* ",trim_title.split("\\s+"))+"*)";
		preparedStatement.setString(4, titleString);

		preparedStatement.setString(5, trim_title);
		Integer threshold = (trim_title.length() /3);
		preparedStatement.setString(6,  threshold.toString() );
		Integer year = NumberUtil.parseIntWithDefault(request.getParameter("MovieYear"),-1);

		preparedStatement.setInt(7, year);
		preparedStatement.setInt(8, year);
		return preparedStatement;

	}

	private PreparedStatement getAndroidMovieTile(HttpServletRequest request, Connection dbcon, String titleQuery) throws java.sql.SQLException
	{
		String query = "SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director, R.rating AS rating FROM ratings R RIGHT JOIN movies M ON M.Id=R.movieId WHERE MATCH(M.title) AGAINST ( ? IN BOOLEAN MODE)  OR ed(M.title, ?) <= ? GROUP BY M.id, R.rating ";

		String trim_title = titleQuery.trim();
		query = paginationQuery(request, query);
		PreparedStatement preparedStatement = dbcon.prepareStatement(query);
		String titleString = "("+ String.join( "* ",trim_title.split("\\s+"))+"*)";
		preparedStatement.setString(1, titleString);

		preparedStatement.setString(2, trim_title);
		Integer threshold = (trim_title.length() /3);
//        if (threshold >=3)
//            threshold=3;
		preparedStatement.setString(3,  threshold.toString() );
		System.out.println(preparedStatement);
		return preparedStatement;

	}
	private PreparedStatement getStatement(HttpServletRequest request, Connection dbcon) throws java.sql.SQLException
	{
		String all= request.getParameter("all");
		if(all!=null && all.equals("1"))
			return getStatementBrowseAll(request, dbcon);

		String is_android = request.getParameter("is_android");
		String movieTitle = request.getParameter("MovieTitle");
		if(is_android!=null && is_android.equals("1")  && movieTitle!=null && !movieTitle.isEmpty())
			return getAndroidMovieTile(request, dbcon,movieTitle);

		String genre = request.getParameter("genre");
		if (genre!=null && !genre.isEmpty()) return getStatementByGenre(request, dbcon, genre);
		String startsWith = request.getParameter("startsWith");

		if (startsWith!=null && !startsWith.isEmpty()) return getStatementAlphabet(request, dbcon, startsWith);

		if (movieTitle!=null && !movieTitle.isEmpty()) return getStatementByTitle(request, dbcon, movieTitle);
		String query = "SELECT M.id AS id, M.title AS title, M.year AS year, M.director AS director,\n" +
				"       R.rating AS rating\n" +
				"FROM ratings R RIGHT JOIN movies M ON M.Id=R.movieId\n" +
				"WHERE\n" +
				"  (M.id IN (\n" +
				"    SELECT movieId FROM stars_in_movies T INNER JOIN stars S ON T.starId=S.id AND S.name LIKE ? \n" +
				"  ) OR ('%%'=?))\n" +
				"   M.director LIKE ? AND (-1=? OR M.year=?) GROUP BY M.id, R.rating";

		query = paginationQuery(request, query);
		PreparedStatement preparedStatement = dbcon.prepareStatement(query);
		String starName = request.getParameter("StarName");
		if (starName==null) starName="";
		starName = "%"+ starName.trim()+"%";
		preparedStatement.setString(1, starName);
		preparedStatement.setString(2, starName);
		String director = request.getParameter("Director");
		preparedStatement.setString(3, "%"+director.trim()+"%");
		Integer year = NumberUtil.parseIntWithDefault(request.getParameter("MovieYear"),-1);

		preparedStatement.setInt(4, year);
		preparedStatement.setInt(5, year);
		return preparedStatement;


	}

	private String paginationQuery(HttpServletRequest request, String query)
	{
		String orderBy = request.getParameter("orderBy");

		Integer offset = Math.max(0,NumberUtil.parseIntWithDefault(request.getParameter("offset"), 0) );
		Integer defaultLimit = NumberUtil.parseIntWithDefault(
				getServletContext().getInitParameter("ResultListDefaultLimit"), 0);
		Integer limit =  NumberUtil.parseIntWithDefault(request.getParameter("limit"), defaultLimit);

		String isASC = request.getParameter("isASC");
		if (isASC!=null && isASC.equals("1")) isASC="ASC";
		else isASC="DESC";
		query+= String.format(" ORDER BY %s %s, M.title ASC LIMIT %d OFFSET %d ", orderBy, isASC, limit, offset);
		return query;

	}
	private void logResult(String result) throws FileNotFoundException {
		String logPath = getServletContext().getRealPath("/") +"test.txt";
		File f = new File(logPath);

		PrintWriter writer = null;
		if ( f.exists() && !f.isDirectory() ) {
			writer = new PrintWriter(new FileOutputStream(new File(logPath), true));
		}
		else {
			writer = new PrintWriter(logPath);
		}
		writer.append(result);
		writer.close();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		doGet(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("application/json"); // Response mime type
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try
		{

			// Time an event in a program to nanosecond precision
			long  startTime = System.nanoTime();

			// Get a connection from dataSource
			Context initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
			Connection dbcon = ds.getConnection();

//			Connection dbcon = dataSource.getConnection(); // direct connection without pooling
			PreparedStatement statement = getStatement(request, dbcon);
			ResultSet resultSet = statement.executeQuery();
			long TJendTime = System.nanoTime();
			long TJelapsedTime = TJendTime - startTime; // elapsed time in nano seconds. Note: print the values in nano seconds
			JsonArray jsonArray = new JsonArray();
			while (resultSet.next())
			{
				String id = resultSet.getString("id");
				String title = resultSet.getString("title");//(each hyperlinked)
				String year = resultSet.getString("year");
				if (year==null) year = "unknown";
				String director = resultSet.getString("director");
				String genres = MoviedbUtil.getGenresString(dbcon, id);
				if (genres==null) genres = "unknown";
				JsonArray stars = MoviedbUtil.getMovieStars(dbcon, id);//resultSet.getString("stars");//(each hyperlinked)
				String rating = resultSet.getString("rating");
				if (rating==null) rating="-";
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", id);
				jsonObject.addProperty("movie_title", title);
				jsonObject.addProperty("movie_year", year);
				jsonObject.addProperty("movie_director", director);
				jsonObject.addProperty("genres", genres);
				jsonObject.add("stars", stars);
				jsonObject.addProperty("rating", rating);

				jsonArray.add(jsonObject);
			}


			// write JSON string to output
			JsonObject output = new JsonObject();
			output.addProperty("status", "success");
			output.add("data", jsonArray);
			out.write(output.toString());
			// set response status to 200 (OK)
			response.setStatus(200);
			resultSet.close();
			statement.close();
			dbcon.close();

			long TSendTime = System.nanoTime();
			long TSelapsedTime = TSendTime - startTime;
			logResult(String.format("%d %d\n", TJelapsedTime, TSelapsedTime));

		}
		catch (Exception e)
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			e.printStackTrace();
			jsonObject.addProperty("message",e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(200);
		}
	}

}
