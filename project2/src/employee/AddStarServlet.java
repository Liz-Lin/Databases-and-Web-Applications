package employee;

import Utils.MoviedbUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "employee.AddStarServlet ", urlPatterns = "/api/_dashboard/add-star")
public class AddStarServlet extends HttpServlet {
    @Resource(name = "jdbc/write")
    private DataSource dataSource;
    public String mv_pre="";
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        //String id = request.getParameter("id");
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");
        JsonObject jsonObject = new JsonObject();

        try{
            Connection connection = dataSource.getConnection();
            if(name.equals("")){
                jsonObject.addProperty("message", "Please enter star's name");
            }
            else {
                String query = "show tables like 'IDS'; ";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                System.out.println(query);
                boolean is_IDS = resultSet.next();


                if(!is_IDS)
                {

                    System.out.println("CREATING TABLE IDS");
                    Integer new_star_id =0, new_movie_id =0, new_genre_id =0;
                    // need to create table IDS with mID, sID

                    query = "CREATE TABLE IDS ( mID INTEGER NOT NULL ,  sID INTEGER NOT NULL, gID INTEGER NOT NULL );";
                    statement =connection.prepareStatement(query);
                    int is_create_ids = statement.executeUpdate();
                    System.out.println(query);


                    //need to get current max star id
                    query = "select max(id) as max_star  from stars;";
                    resultSet = connection.prepareStatement(query).executeQuery();
                    System.out.println(query);
                    String max_star ="";
                    if(resultSet.next())
                    {
                        max_star = resultSet.getString("max_star");
                        System.out.println("current max star is:" + max_star);

                    }
                    new_star_id = Integer.parseInt(max_star.substring(2));
                    new_star_id=new_star_id+2;
                    System.out.println("new max star is:" + new_star_id);


                    //need to get current max movie id
                    query = "select max(id) as max_movie  from movies;";
                    resultSet = connection.prepareStatement(query).executeQuery();
                    System.out.println(query);
                    String max_movie ="";
                    if(resultSet.next())
                    {
                        max_movie = resultSet.getString("max_movie");
                        System.out.println("current max movie is:" + max_movie);

                    }
                    String[] part = max_movie.split("(?<=\\D)(?=\\d)");
                    mv_pre = part[0];
                    new_movie_id = Integer.parseInt(part[1]);
                    //new_movie_id = Integer.parseInt(max_movie.substring(2));
                    new_movie_id++;
                    System.out.println("new max movie is:" + new_movie_id);


                    //need to get current max genre id
                    query = "select max(id) as max_genre  from genres;";
                    resultSet = connection.prepareStatement(query).executeQuery();
                    System.out.println(query);
                    Integer max_genre =0;
                    while(resultSet.next())
                    {
                        max_genre = resultSet.getInt("max_genre");
                        System.out.println("current max genres is:" + max_genre);

                    }

                    new_genre_id = max_genre +1;
                    System.out.println("new max genre is:" + new_genre_id);

                    query = "insert into IDS values("+new_movie_id + " , " + new_star_id + ","+ new_genre_id+" );";
                    statement = connection.prepareStatement(query);
                    int is_insert_IDS = statement.executeUpdate();

                    System.out.println(query);


                    //insert and update the new
                    new_star_id--;
                    String nm_starid = "nm" + String.valueOf(new_star_id);
                    query = "insert into stars values ('"+nm_starid +"' , '"+ name+ "',"+birthYear+ "); ";
                    statement = connection.prepareStatement(query);
                    int is_finish_insert_actor = statement.executeUpdate();

                    System.out.println(query);
                    if(is_finish_insert_actor == 1)
                    {
                        jsonObject.addProperty("status", "success");
                        jsonObject.addProperty("message","create IDS succeed");
                    }
                    else {
                        jsonObject.addProperty("status", "fail");
                        jsonObject.addProperty("message","create IDS failed");
                    }

                }
                else
                {
                    System.out.println("TABLE IDS EXISTS");
                    Integer new_star_id = 0;
                    query = "select sID as max_star  from IDS;";
                    resultSet = connection.prepareStatement(query).executeQuery();
                    System.out.println(query);

                    if(resultSet.next())
                    {
                        new_star_id = resultSet.getInt("max_star");

                    }
                    if(birthYear.equals(""))
                        birthYear = null;
                    String nm_starid = "nm" + String.valueOf(new_star_id);

                    query = "insert into stars values ('"+nm_starid +"' , '"+ name+ "',"+birthYear+ "); ";
                    System.out.println("before execute: "+query);
                    statement = connection.prepareStatement(query);
                    int insert_star = statement.executeUpdate();
                    System.out.println(query);

                    new_star_id++;
                    //need to uncheck the safe update option in preference
                    query = "UPDATE IDS SET sID = "+ new_star_id +";";
                    statement = connection.prepareStatement(query);
                    int is_update_IDS = statement.executeUpdate();
                    System.out.println(query);


                    if(is_update_IDS == 1)
                    {
                        jsonObject.addProperty("status", "success");
                        jsonObject.addProperty("message","insert success");
                    }
                    else {
                        jsonObject.addProperty("status", "fail");
                        jsonObject.addProperty("message","insert failed");
                    }

                }

                jsonObject.addProperty("status", "success");
            }
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
