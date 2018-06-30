package employee;

import Utils.MoviedbUtil;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import com.mysql.jdbc.ReplicationDriver;

@WebServlet(name = "employee.AddMovieServlet ", urlPatterns = "/api/_dashboard/add-movie")
public class AddMovieServlet extends HttpServlet{
    @Resource(name = "jdbc/write")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String starBirthYear = request.getParameter("star-birth-year");

        Integer m_year = Integer.parseInt(year);
        Integer s_year=null;
        if (starBirthYear != "")
            s_year =Integer.parseInt(starBirthYear);


        JsonObject jsonObject = new JsonObject();
        try{
            Connection connection = dataSource.getConnection();
            // TODO: add query for add movie function
            //String message = MoviedbUtil.addMovie(connection, id, title, year, director, star, genre);
//            System.out.println("Calling Stored Procedure: add_movie");

            String message="";

            CallableStatement cStmt = connection.prepareCall("{call add_movie(?,?,?,?,?,?,?)}");

            cStmt.setString(1,title);
            cStmt.setInt(2,m_year);
            cStmt.setString(3,director);
            cStmt.setString(4,star);
            cStmt.setInt(5,s_year);
            cStmt.setString(6,genre);
            cStmt.registerOutParameter(7, Types.VARCHAR);

            System.out.println(title+"/"+ m_year+"/"+ director+"/"+ star+"/"+s_year+ "/"+genre);

            boolean hadResults = cStmt.execute();

            message = cStmt.getString(7) + "finished the procedure";
//            System.out.println(message + "finished the procedure");


            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", message);

        }catch (Exception e)
        {
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());
        }
        finally {
            response.getWriter().write(jsonObject.toString());
        }

    }
}
