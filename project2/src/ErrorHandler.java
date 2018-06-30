// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

// Extend HttpServlet class
public class ErrorHandler extends HttpServlet {

	// Method to handle GET method request.
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Analyze the servlet exception
		Throwable throwable = (Throwable)
				request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer)
				request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String)
				request.getAttribute("javax.servlet.error.servlet_name");
//		String message = throwable.getMessage();
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String)
				request.getAttribute("javax.servlet.error.request_uri");

		if (requestUri == null) {
			requestUri = "Unknown";
		}

		// Set response content type
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		String title = "<title>Error/Exception Information</title>";
		String docType =
				"<!DOCTYPE html>\n" +
						"<html lang=\"en\">";
		String meta = "    <meta charset=\"UTF-8\">\n" +
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
				"\n" +
				"    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
				"          integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
				"\n" +
				"    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
				"    <link rel=\"stylesheet\" href=\"CSS/custom.css\">";
		String navBar= "<nav class=\"navbar navbar-expand-lg navbar-light bg-light navbar-fixed-top\">\n" +
				"    <div class=\"container-fluid\">\n" +
				"        <a class=\"navbar-brand\" href=\"/index.html\">Fablix</a>\n" +
				"        <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarSupportedContent\"\n" +
				"                aria-controls=\"navbarSupportedContent\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\n" +
				"            <span class=\"navbar-toggler-icon\"></span>\n" +
				"        </button>\n" +
				"\n" +
				"        <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">\n" +
				"            <ul class=\"navbar-nav mr-auto\">\n" +
				"                <li class=\"nav-item active\">\n" +
				"                    <a class=\"nav-link\" href=\"/index.html\">Home <span class=\"sr-only\"></span></a>\n" +
				"                </li>\n" +
				"                <li class=\"nav-item\">\n" +
				"                    <a class=\"nav-link\" href=\"/search.html\">Advanced Search</a>\n" +
				"                </li>\n" +
				"                <li class=\"nav-item\">\n" +
				"                    <a class=\"nav-link\" target=\"_blank\" href=\"/shopping-cart.html\">\n" +
				"                        <i class=\"fa fa-cart-arrow-down\" aria-hidden=\"true\"></i> shopping cart\n" +
				"                    </a>\n" +
				"                </li>\n" +
				"            </ul>\n" +
				"            <!--<form class=\"form-inline my-2 my-lg-0\">-->\n" +
				"            <!--<input class=\"form-control mr-sm-2\" type=\"search\" placeholder=\"Search\" aria-label=\"Search\">-->\n" +
				"            <!--<button class=\"btn btn-outline-success my-2 my-sm-0\" type=\"submit\">Search</button>-->\n" +
				"            <!--</form>-->\n" +
				"\n" +
				"            <a class=\" my-2 my-lg-0\" href=\"javascript:onClickSignout();\">\n" +
				"                sign out\n" +
				"            </a>\n" +
				"        </div>\n" +
				"    </div>\n" +
				"</nav>\n";
		out.println(docType +
				"<head>" + title + meta+"</head>\n" +
				"<body>\n"+navBar);
		out.println("<div class=\"container\">");
		out.println("<h1>Sorry, a problem occurred...</h1>");
		if (throwable == null && statusCode == null) {
			out.println("<h2>Error information is missing</h2>");

		} else if (statusCode != null) {
			out.println("<p>Status code : " + statusCode+"</p>");
		}
			out.println("<h2>Error information</h2>");
			out.println("Servlet Name : " + servletName + "</br></br>");
			out.println("Exception Type : " + throwable.getClass( ).getName( ) + "</br></br>");
			out.println("The request URI: " + requestUri + "<br><br>");
			out.println("The exception message: " + throwable.getMessage( ));

		out.println("<p>Please return to the <a href=\"/index.html\">Home Page</a><p>.");
		out.println("</div>");
		out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
				"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\"></script>\n" +
				"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
				"<script src=\"util.js\"></script>");
		out.println("</body>");
		out.println("</html>");
	}

	// Method to handle POST method request.
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}