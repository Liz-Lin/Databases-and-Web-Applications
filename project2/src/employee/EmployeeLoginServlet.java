package employee;
import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;
import recaptcha.RecaptchaVerifyUtils;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;


@WebServlet(name = "employee.EmployeeLoginServlet", urlPatterns = "/api/_dashboard/login")
public class EmployeeLoginServlet extends HttpServlet{

    @Resource(name = "jdbc/write")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
//        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        }
        catch (Exception e) {
//            out.println("<html>");
//            out.println("<head><title>Error</title></head>");
//            out.println("<body>");
//            out.println("<p>recaptcha verification error</p>");
//            out.println("<p>" + e.getMessage() + "</p>");
//            out.println("</body>");
//            out.println("</html>");
//            out.close();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "recaptcha verification error: "+e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* connect to database to check the email and password
         */
        try
        {
            // create database connection
            Connection connection = dataSource.getConnection();
            // declare statement

            String query = String.format("SELECT * FROM employees WHERE email='%s' LIMIT 1", email);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            if (resultSet.next())
            {// record found
                String encryptedPassword = resultSet.getString("password");
                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if (success){
                    System.out.println("success");
                    String fullname = resultSet.getString("fullname");
                    request.getSession().setAttribute("employee", new Employee(email, fullname ));
                    responseJsonObject.addProperty("status", "success");
                }
                else //password is correct
                {// incorrect password
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            else
            {// no user
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }
            resultSet.close();
            statement.close();
            connection.close();

        }
        catch (Exception e)
        {// other errors
            e.printStackTrace();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
        }
        finally
        {
            response.getWriter().write(responseJsonObject.toString());
        }
    }
}
