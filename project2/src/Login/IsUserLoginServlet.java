package Login;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Login.IsUserLoginServlet", urlPatterns = "/api/is_user_login")
public class IsUserLoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        JsonObject responseJsonObject = new JsonObject();

        responseJsonObject.addProperty("is_user_login",
                request.getSession().getAttribute("user")!=null);
            response.getWriter().write(responseJsonObject.toString());
        response.setStatus(200);
    }
}
