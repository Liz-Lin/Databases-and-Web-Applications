package AutoComplete;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet(name = "AutoComplete.MovieAutoCompleteServlet ", urlPatterns = "/api/movieAutoComplete")
public class MovieAutoCompleteServlet extends HttpServlet{
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            String star_query =  String.join( "* ",query.split("\\s+"))+"*"; // new format for mysql query
            // search on marvel heros and DC heros and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
//           String mysqlQuery = "SELECT MATCH(title) AGAINST (? IN BOOLEAN MODE)AS score, id, title, year, director\n" +
//                    "FROM movies HAVING score> 0 ORDER BY score DESC, title ASC LIMIT 10";
            String auto_fuzzy_query = "SELECT id, title, year, director FROM movies WHERE MATCH(title) AGAINST ( ? IN BOOLEAN MODE)  OR ed(title, ?) <= ? " +
                    "ORDER BY title ASC LIMIT 10 ;";

            Connection  dbcon = dataSource.getConnection();
            PreparedStatement statement = dbcon.prepareStatement(auto_fuzzy_query);
            //statement.setString(1,  star_query);
            statement.setString(1,  star_query);
            statement.setString(2,  query);
            Integer threshold = (query.length() /4);
            if (threshold >=3)
                threshold=3;
            statement.setString(3,  threshold.toString() );
            System.out.println(statement);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {

                String movieID = resultSet.getString("id");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                jsonArray.add(generateJsonObject(movieID, title, year, director));

            }


            response.getWriter().write(jsonArray.toString());
            resultSet.close();
            statement.close();
            dbcon.close();
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }

    }

    /*
     * Generate the JSON Object from hero and category to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "category": "marvel", "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieId, String title, String year, String director) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("category", "Movie");
        additionalDataJsonObject.addProperty("movieID", movieId);
//        additionalDataJsonObject.addProperty("movieYear", year);
//        additionalDataJsonObject.addProperty("movieDirector", director);
        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

}
