package Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

public class MoviedbUtil {

	public static JsonArray getMovieStars(Connection dbcon, String movieID) throws java.sql.SQLException
	{
		JsonArray jsonArray = new JsonArray();
		String query = "SELECT s.id AS id, s.name AS name " +
				"FROM stars_in_movies m INNER JOIN stars s ON m.starId=s.id WHERE m.movieId=?;";
		PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, movieID);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next())
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("star_id", resultSet.getString("id"));
			jsonObject.addProperty("star_name", resultSet.getString("name"));
			jsonArray.add(jsonObject);
		}

		resultSet.close();
		statement.close();
		return jsonArray;
	}

	public static JsonArray getMovieStarsByList(Connection dbcon, String starList) throws java.sql.SQLException
	{


		JsonArray starArray = new JsonArray();
		String deli = "[,]";
		String[] star_names = starList.split(deli);
		for (int i =0; i<star_names.length; ++i)
		{
			String query = "SELECT s.id as starId\n" +
					"FROM stars s\n" +
					"WHERE s.name = '" +star_names[i]+"';";
			PreparedStatement statement = dbcon.prepareStatement(query);

			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			String star_id = resultSet.getString("starId");

			JsonObject starObj = new JsonObject();
			starObj.addProperty("star_id", star_id);
			starObj.addProperty("star_name", star_names[i]);
			starArray.add(starObj);
			resultSet.close();
		}

		return starArray;
	}



	public static JsonArray getGenres(Connection dbcon, String movieID) throws java.sql.SQLException
	{
		JsonArray jsonArray = new JsonArray();
		String query = "SELECT g.id AS id, g.name AS name FROM genres g " +
				"INNER JOIN genres_in_movies m on m.genreId = g.id WHERE m.movieId=?";
		PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, movieID);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next())
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("genre_id", resultSet.getString("id"));
			jsonObject.addProperty("genre_name", resultSet.getString("name"));
			jsonArray.add(jsonObject);
		}

		resultSet.close();
		statement.close();
		return jsonArray;
	}
	public static String getGenresString(Connection dbcon, String movieID) throws java.sql.SQLException
	{
		String query = "SELECT GROUP_CONCAT(DISTINCT G.name ORDER BY G.name ASC SEPARATOR ', ') AS genres " +
				"FROM genres G, genres_in_movies J WHERE G.id=J.genreId AND J.movieId=?";
		PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, movieID);
		ResultSet resultSet = statement.executeQuery();
		String result = "";
		if(resultSet.next())
		{
			result = resultSet.getString("genres");
		}
		resultSet.close();
		statement.close();
		return result;
	}

	public static Boolean hasCreditCard(Connection dbcon,
	                                    String cardNum, String firstName, String lastName, String expiry)
			throws java.sql.SQLException
	{
		String query = "SELECT * FROM creditcards " +
				"WHERE id='"+cardNum+ "' AND firstName='"+firstName + "' AND lastName='"+lastName+
				"' AND DATE(expiration)='"+expiry+"';";
//		System.out.println(query);
		PreparedStatement statement = dbcon.prepareStatement(query);
		ResultSet resultSet = statement.executeQuery();
		return resultSet.next();
	}

	public static void addToSale(Connection dbcon, String customerId, String movieId)throws java.sql.SQLException
	{
		String query = "INSERT INTO sales(customerId, movieId, saleDate) VALUES(490007,'tt0399582', '2005/01/05');";

	}


	public static JsonArray getDBmetadata(Connection dbcon)throws java.sql.SQLException {/*
	https://www.progress.com/blogs/jdbc-tutorial-extracting-database-metadata-via-jdbc-driver
	*/
		DatabaseMetaData dbmd = dbcon.getMetaData();
		ResultSet resultSet = dbmd.getTables(null, null, null, new String[]{"TABLE"});
		JsonArray metadata = new JsonArray();
		while (resultSet.next()) {
			JsonObject table = new JsonObject();
			String tableName = resultSet.getString("TABLE_NAME");
			ResultSet columns = dbmd.getColumns(null, null, tableName, null);
			JsonArray attrs = new JsonArray();
			while (columns.next()) {
				JsonObject attr = new JsonObject();
				String columnName = columns.getString("COLUMN_NAME");
				String datatype = columns.getString("TYPE_NAME");
				attr.addProperty("col-name", columnName);
				attr.addProperty("data-type", datatype);
				attrs.add(attr);
			}
			columns.close();
			table.addProperty("table-name", tableName);
			table.add("attrs", attrs);
			metadata.add(table);
		}
		resultSet.close();
		return metadata;
	}


	public static String addMovie(Connection dbcon, String id, String title,
								String year, String director, String star, String genre) throws  java.sql.SQLException{
		System.out.println("Calling Stored Procedure: add_movie");

		String message="";


		CallableStatement cStmt = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?,?)}");

		cStmt.setString(1,title);
		Integer m_year = Integer.parseInt(year);
		cStmt.setInt(2,m_year);
		cStmt.setString(3,director);
		cStmt.setString(4,star);
		Integer s_year =1996;
		cStmt.setInt(5,s_year);
		cStmt.setString(6,genre);
		cStmt.registerOutParameter("message", Types.VARCHAR);

		System.out.println(title+"/"+ m_year+"/"+ director+"/"+ star+"/"+s_year+ "/"+genre);

		cStmt.execute();
		System.out.println("finished");



		message = cStmt.getString(7);
		System.out.println(message + "finished");

		return message;

	}
}
