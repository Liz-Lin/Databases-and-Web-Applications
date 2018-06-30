package Login;

import com.google.gson.JsonObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpSession;

@WebServlet(name = "Login.SignoutServlet", urlPatterns = "/api/signout")
public class SignoutServlet extends HttpServlet {
	private static final long serialVersionUID = 8L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("application/json");
		HttpSession session = request.getSession(false);
		JsonObject responseJsonObject = new JsonObject();
		if (session != null) {
			session.invalidate();
			responseJsonObject.addProperty("status", "success");
			responseJsonObject.addProperty("message", "success");
			response.setStatus(200);
		}
		else {
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Session not exists");
			response.setStatus(500);
		}
		response.getWriter().write(responseJsonObject.toString());
	}
}
