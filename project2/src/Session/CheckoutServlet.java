package Session;

import Login.User;
import Utils.MoviedbUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


@WebServlet(name = "Session.CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 6L;
	@Resource(name = "jdbc/write")
	private DataSource dataSource;

	private JsonArray addToSale(Connection dbcon , JsonObject previousItems, Integer customerId)throws java.sql.SQLException
	{

		String query = "INSERT INTO sales(customerId, movieId, saleDate) VALUES(?, ?, CURDATE())";
		PreparedStatement statement = dbcon.prepareStatement(query);

		JsonArray orderDetail = new JsonArray();
		for(String movieId: previousItems.keySet()){
			JsonObject movieInfo = previousItems.getAsJsonObject(movieId);
			String movieTitle = movieInfo.get("item-name").toString();
			int movieQty = Integer.parseInt(movieInfo.get("count").toString());
			JsonObject movieSaleInfo = new JsonObject();
			movieSaleInfo.addProperty("qty", movieQty);
			movieSaleInfo.addProperty("movie-id", movieId);
			movieSaleInfo.addProperty("movie-title", movieTitle);
			JsonArray saleIdList = new JsonArray();
			for (int i = 0; i < movieQty; i++)
			{
				statement.setInt(1, customerId);
				statement.setString(2, movieId);
				statement.executeUpdate();
				statement = dbcon.prepareStatement("SELECT LAST_INSERT_ID();");
				ResultSet salesIdResultSet = statement.executeQuery();
//				dbcon.commit();
				salesIdResultSet.next();
				String salesId = salesIdResultSet.getString("LAST_INSERT_ID()");
				salesIdResultSet.close();
				saleIdList.add(salesId);
			}
			movieSaleInfo.add("sale-id-list", saleIdList);
			orderDetail.add(movieSaleInfo);
		}

		statement.close();

		return orderDetail;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String cardNum = request.getParameter("number");
		String firstName = request.getParameter("first-name");
		String lastName = request.getParameter("last-name");
		String expiry = request.getParameter("expiration");
		JsonObject responseJsonObject = new JsonObject();
		try
		{
			Connection dbcon = dataSource.getConnection();
			if (MoviedbUtil.hasCreditCard(dbcon, cardNum, firstName, lastName, expiry)){
				HttpSession session = request.getSession();
				JsonObject previousItems = (JsonObject) session.getAttribute("previousItems");
				if (previousItems == null || previousItems.toString().equals("{}")){
					responseJsonObject.addProperty("status", "fail");
					responseJsonObject.addProperty("message", "Your cart is empty");
					response.setStatus(200);
				}
				else{
					int customerId = ((User)session.getAttribute("user")).getId();
					JsonArray orderDetail = addToSale(dbcon, previousItems, customerId);
					session.removeAttribute("previousItems");// remove current purchase
					session.setAttribute("recentOrderDetail", orderDetail);
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("message", "success");
					response.setStatus(200);
				}
			}
			else {
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Credit card information not found");
			}
		}catch (Exception e)
		{
			e.printStackTrace();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", e.getMessage());
			response.setStatus(500);
		}finally
		{
			response.getWriter().write(responseJsonObject.toString());
		}


	}
}
