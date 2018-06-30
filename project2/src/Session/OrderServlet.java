package Session;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "Session.OrderServlet", urlPatterns = "/api/order")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 7L;

	public void doGet(HttpServletRequest request,
	                  HttpServletResponse response) throws IOException
	{
		HttpSession session = request.getSession();
		Object orderDetail = session.getAttribute("recentOrderDetail");
		if (orderDetail==null){
			response.setStatus(500);
		}
		else {
			response.getWriter().write(orderDetail.toString());
			response.setStatus(200);
		}
	}
}
