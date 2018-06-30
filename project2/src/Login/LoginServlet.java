package Login;

import recaptcha.RecaptchaVerifyUtils;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "Login.LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        //String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        JsonObject responseJsonObject = new JsonObject();
        // Verify reCAPTCHA
        try {
            //RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "recaptcha verification error: " + e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* connect to database to check the email and password
         */
        try {
            // create database connection
            Connection connection = dataSource.getConnection();
            // declare statement

            String query = String.format("SELECT * FROM customers WHERE email='%s' LIMIT 1", email);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            if (resultSet.next()) {// record found
                String encryptedPassword = resultSet.getString("password");
                boolean success = false;
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success)//password is correct
                {// set this user into the session
                    int customerId = Integer.parseInt(resultSet.getString("id"));
                    request.getSession().setAttribute("user", new User(customerId, email));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {// incorrect password
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            } else {// no user
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {// other errors
            e.printStackTrace();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
        } finally {
            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(200);
        }
    }
}
