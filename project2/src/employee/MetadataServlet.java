package employee;

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


@WebServlet(name = "employee.MetadataServlet", urlPatterns = "/api/_dashboard/metadata")
public class MetadataServlet extends HttpServlet {

    @Resource(name = "jdbc/write")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            JsonArray metadata = MoviedbUtil.getDBmetadata(dbcon);

            // write JSON string to output
            out.write(metadata.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }

}
