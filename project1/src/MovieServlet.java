

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/top20ratings")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//maybe: response.getWriter().append("Served at: ").append(request.getContextPath());
		String loginUser = "jenny";
        String loginPasswd = "cs122b";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">\n" +
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">");


        // bootstrap css
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">");
        // bootstrap JavaScript
        out.println("<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\n" +
				"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>\n" +
				"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>");
		// custom css
		out.println("<link rel=\"stylesheet\" href=\"CSS/fablix_style.css\">");
        out.println("<title>Fabflix</title>");
		out.println("</head>");
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String TOP20_RATINGS =" SELECT M.title AS Title, M.year AS Year, M.director AS Director, \n" +
						" GROUP_CONCAT(DISTINCT G.name ORDER BY G.name ASC SEPARATOR ', ') AS Genres,\n" +
						"GROUP_CONCAT( DISTINCT  S.name ORDER BY S.name ASC SEPARATOR ', ') AS Stars, R.rating AS Ratings\n" +
						"FROM genres G, genres_in_movies J , stars_in_movies P, stars S, ratings R, movies M\n" +
						"INNER JOIN (SELECT R2.movieId\n" +
						"\t\t\t\t\tFROM ratings R2\n" +
						"\t\t\t\t\tORDER BY R2.rating  \n" +
						"\t\t\t\t\tDESC\n" +
						"\t\t\t\t\tlimit 20 ) AS T\n" +
						"ON M.id = T.movieId \n" +
						"WHERE M.id = R.movieId AND M.id = J.movieId AND J.genreId = G.id AND M.id = P.movieId AND P.starId = S.id\n" +
						"GROUP BY M.id, R.rating \n" +
						"ORDER BY R.rating DESC, M.title\n" +
						"ASC;";
    
        		// execute query
        		ResultSet resultSet = statement.executeQuery(TOP20_RATINGS);
        		
        		

        		out.println("<body>");
        		out.println("<div class=\"container\">");
        		out.println("<div class=\"row\"><h1 class=\"title\">Top 20 Rating Movies</h1></div>");
        		out.println("<div class=\"row\">");
        		out.println("<table class=\"table table-striped table-dark\">");
        		
        		// add table header row
				out.println("<thead>");
        		out.println("<tr>");
        		out.println("<th scope=\"col\">#</th>");
        		out.println("<th>Title</th>");
        		out.println("<th scope=\"col\">Year</th>");
        		out.println("<th scope=\"col\">Director</th>");
        		out.println("<th scope=\"col\">List of Genres</th>");
        		out.println("<th scope=\"col\">List of Stars</th>");
        		out.println("<th scope=\"col\">Ratings</th>");
        		out.println("</tr>");
				out.println("</thead>");
        		out.println("<tbody>");
        		int rank = 1;
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String Title = resultSet.getString("Title");
        			String Year = resultSet.getString("Year");
        			String Director = resultSet.getString("Director");
        			String Genres = resultSet.getString("Genres");
        			String Stars = resultSet.getString("Stars");
        			String Ratings = resultSet.getString("Ratings");
        			
        			
        			out.println("<tr>");
					out.println("<th scope=\"row\">" + rank + "</th>");
        			out.println("<td>" + Title + "</td>");
        			out.println("<td>" + Year + "</td>");
        			out.println("<td>" + Director + "</td>");
        			out.println("<td>" + Genres + "</td>");
        			out.println("<td>" + Stars + "</td>");
        			out.println("<td>" + Ratings + "</td>");
        			out.println("</tr>");
        			rank++;
        		}
				out.println("</tbody>");
        		out.println("</table>");
        		out.println("</div>");
        		out.println("</div>");
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
				
		        
		
	}

}




