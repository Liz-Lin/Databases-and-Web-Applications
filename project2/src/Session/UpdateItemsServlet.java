package Session;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "Session.UpdateItemsServlet", urlPatterns = "/api/update-item")
public class UpdateItemsServlet extends HttpServlet {
	private static final long serialVersionUID = 5L;


	public void doGet(HttpServletRequest request,
	                  HttpServletResponse response) throws IOException
	{
		HttpSession session = request.getSession();
		JsonObject previousItems = (JsonObject) session.getAttribute("previousItems"); // Retrieve data named "previousItems" from session

		// Get a instance of current session on the request
		// If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
		if (previousItems == null)
		{
			previousItems = new JsonObject();
			session.setAttribute("previousItems", previousItems); // Add the newly created ArrayList to session, so that it could be retrieved next time
		}
		String newItemId = request.getParameter("itemId");
		String newItemName = request.getParameter("itemName");
		int count = Utils.NumberUtil.parseIntWithDefault(request.getParameter("count"), -1);

		String op = request.getParameter("op");
		response.setContentType("application/json"); // Response mime type
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();


		// In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we lock the ArrayList while updating
		synchronized (previousItems)
		{

			if (newItemId != null && newItemName != null)
			{
				JsonObject newItem = previousItems.getAsJsonObject(newItemId);
				if (count < 0)
				{
					// do nothing
				}
				else if (newItem == null && count > 0)
				{// same for both add and update operations
					newItem = new JsonObject();
					newItem.addProperty("item-name", newItemName);
					newItem.addProperty("count", count);
					previousItems.add(newItemId, newItem);
				}
				else if (count > 0)
				{// update count
					if (op.equals("add")) newItem.addProperty("count", count + newItem.get("count").getAsInt());
					else if (op.equals("update")) newItem.addProperty("count", count);
					previousItems.add(newItemId, newItem);
				}
				else if (op.equals("update") && count == 0 && newItem != null) {
					previousItems.remove(newItemId);// delete item
				}
				session.setAttribute("previousItems", previousItems);
			}
		}
		out.write(previousItems.toString());
		// Display the current previousItems ArrayList

		response.setStatus(200);
		out.close();
	}

}

