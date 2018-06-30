import java.sql.*;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static Integer lowest_sid=0;
    //public static Integer lowest_mid=0;

    //helper data structers to save query multiple times
    //<movieID, <STAR_NAME, STAR(ID, NAME, BIRTHYEAR)>>
    public static HashMap<String, HashMap<String, Actor>> my_mvID_Actor = new HashMap<String, HashMap<String, Actor>>();
    //<star_name, star_id> pair for checking if the star alraedy in "database"
    public static HashMap<String, String>  sname_sid_pair = new HashMap<String, String>();

    public static List<String> movieId_list = new ArrayList<String>();

    // save all the data till process all the files
    // then insert them all at once.
    //<movieId, starId>
    public static HashMap<String, String> add_to_star_in_mv = new HashMap<String, String>();
    //<name,  <id, name,birthYear>>
    public static HashMap<String, Actor> add_to_stars = new HashMap<String, Actor>();



    //to get all the stars_in_movies with stars' detailed info (name & birthYear)
    private static void load_mv_star_from_db (Connection conn)
    {

        String query = "select m.id as mvId , s.id as sId, s.name as s_name, s.birthYear as s_year\n" +
                "from movies m, stars_in_movies p, stars s\n" +
                "where m.id = p.movieId AND p.starId = s.id;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            String starId ="", movieId = "", starName = "";
            Integer birthYear = null ;
            while (resultSet.next()) {
                movieId = resultSet.getString("mvId");
                starId = resultSet.getString("sId");
                starName = resultSet.getString("s_name");
                birthYear = resultSet.getInt("s_year");
                if(!my_mvID_Actor.containsKey(movieId))
                {
                    my_mvID_Actor.put(movieId, new HashMap<String, Actor>());
                    my_mvID_Actor.get(movieId).put(starName,new Actor(starId, starName,birthYear) );
                }
                else
                {
                    if(!my_mvID_Actor.get(movieId).containsKey(starName))
                        my_mvID_Actor.get(movieId).put(starName,new Actor(starId, starName,birthYear) );

                }
            }


        }
        catch (Exception e)
        {
            System.out.println("failed to load movie star star in movie from db");

        }
    }

    private static void load_all_movie_id (Connection conn)
    {
        String query = "SELECT id FROM movies;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            String movieId;

            while (resultSet.next()) {
                movieId = resultSet.getString("id");
                movieId_list.add(movieId);
            }

        }
        catch (Exception e)
        {
            System.out.println("failed to load movie ID");

        }
    }

    private static void load_star_name_id_pairs (Connection conn)
    {
//<star_name, star_id>
        String query = "select name, id from stars;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            String starId ="", starName = "";

            while (resultSet.next()) {
                starId = resultSet.getString("id");
                starName = resultSet.getString("name");
                sname_sid_pair.put(starName, starId);
            }

//            Set sname_sid_set = sname_sid_pair.entrySet();
//            Iterator sname_id_it = sname_sid_set.iterator();
//            while(sname_id_it.hasNext())
//            {
//                Map.Entry mvid_sid_pair = (Map.Entry) sname_id_it.next();
//                System.out.println("\n\n\nMovie id Star id pairs!");
//                System.out.println("mv: " + mvid_sid_pair.getKey().toString() + "star: " + mvid_sid_pair.getValue().toString());
//            }

        }
        catch (Exception e)
        {
            System.out.println("failed to load star");

        }
    }

    //add all additional stars in movie from cast.xml file
    private static void add_parse_cast (Connection conn, String path)
    {
        try{

            System.out.println("\n\nIN ADD PARSE CAST ");
            parse_cast xml_mvID_actor = new parse_cast();
            Set movie_actor_set = xml_mvID_actor.getMyMovie_Actors(path).entrySet();
            Iterator movie_actor_iter = movie_actor_set.iterator();

            while (movie_actor_iter.hasNext()) {
                Map.Entry movie_actor = (Map.Entry) movie_actor_iter.next();
                String movieId = (String)movie_actor.getKey();
                //System.out.println("\n\nMovie: " + movieId);
                ArrayList<String > actor_list= (ArrayList<String >)movie_actor.getValue();
                //System.out.println("Actor: " + actor_list.toString());
                for(String starName : actor_list)// loop through all the actors for this movie
                {
                    if(starName == null)
                        continue;
                    if(!movieId_list.contains(movieId))//no movie in movies table
                    {
                        //System.out.println("No such movie corresponding to ID: " + movieId);

                    }

                    else
                    {
                        String new_star_id ;
                        if (sname_sid_pair.containsKey(starName))//have this actor
                        {
                            new_star_id = sname_sid_pair.get(starName);
                        }
                        else
                        {
                            new_star_id = "nm" + String.valueOf(lowest_sid);
                            lowest_sid++;
                            add_to_stars.put(starName, new Actor(new_star_id, starName, null));
                        }

                        if(!my_mvID_Actor.containsKey(movieId) )//doesnt contain movie -star relationship
                        {
                            add_to_star_in_mv.put(movieId,new_star_id);
                            //System.out.println("insert:     "+ movieId + " "+ new_star_id);
                        }
                        else {
                            if(my_mvID_Actor.get(movieId).containsKey(starName))//there is movie-actor pair
                                System.out.println("Relation " + movieId  +" & " + starName +" already exist!");
                            else {
                                add_to_star_in_mv.put(movieId, new_star_id);
                                System.out.println("insert:     " + movieId + " " + new_star_id);
                            }
                        }





                    }
                }

            }
            System.out.println("Offically load all the movie-actor pairs from cast.xml!");

        }catch(Exception e)
        {
            System.out.println("Faild to load all the movie-actor pairs from cast.xml!");

        }
    }

    private static void getMaxStarId_MovieId(Connection conn) throws java.sql.SQLException
    {


        try {
            String query = "SELECT MAX(id) FROM stars;";
            String num = null;
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                num = resultSet.getString("MAX(id)");
                lowest_sid = Integer.parseInt(num.substring(2));
                lowest_sid++;
                System.out.println("lowest_star_id: " + lowest_sid.toString());
            }

//            query = "SELECT MAX(id) FROM movies;";
//            statement = conn.prepareStatement(query);
//            resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                num = resultSet.getString("MAX(id)");
//                lowest_mid = Integer.parseInt(num.substring(2));
//                lowest_mid++;
//                System.out.println("lowest_movie_id: " + lowest_mid.toString());
//            }

        }
        catch (Exception e)
        {
            System.out.println("failed to get max id out of table star");

        }

    }


    private static void load_parse_actor(Connection conn,String actorXMLpath)
    {
        parse_actor parser = new parse_actor();
        //no longer run the examples which include print the parsed datas --slow
        Iterator<Actor> actorList = parser.getActors(actorXMLpath).iterator();

        try
        {
            while(actorList.hasNext())
            {
                Actor actor = actorList.next();
                if(sname_sid_pair.containsKey(actor.getName()))
                    System.out.println(actor.getName() +" already exists!");
                else
                {
                    String new_star_id = "nm" + String.valueOf(lowest_sid);
                    lowest_sid++;
                    if(add_to_stars.containsKey(actor.getName()))
                    {
                        System.out.println(actor.getName() +" already in the adding list!");
                    }
                    else
                        add_to_stars.put(actor.getName(), new Actor(new_star_id,actor.getName(), actor.getBirthYear()));
                }

            }

            System.out.println("Offically load all the actors from actor.xml!");

        }catch (Exception e)
        {
            System.out.println("failed to load parse_actor!");
            e.printStackTrace();
        }

    }

    private static void insertStars(Connection conn)
    {

        Set actor_set = add_to_stars.entrySet();
        Iterator actor_list = actor_set.iterator();

        PreparedStatement actorStatement = null;
        String actorQuery ="INSERT INTO  stars (id, name, birthYear) VALUES (?,?,?);" ;
        int[] iNoRows=null;
        byte[] id = null;

        try
        {

            actorStatement = conn.prepareStatement(actorQuery);

            while(actor_list.hasNext())
            {

                Map.Entry name_actor = (Map.Entry)actor_list.next();
                Actor new_actor = (Actor) name_actor.getValue();
                String new_star_id = "nm" + String.valueOf(lowest_sid);
                lowest_sid++;
                actorStatement.setString(1, new_star_id);
                actorStatement.setString(2, new_actor.getName());

                if (new_actor.getBirthYear()!= null)
                    actorStatement.setInt(3, new_actor.getBirthYear());
                else
                    actorStatement.setNull(3, Types.INTEGER);
                actorStatement.addBatch();


            }
            iNoRows = actorStatement.executeBatch();

            System.out.println("Offically insert all the actors from xml files!");

        }catch (Exception e)
        {
            System.out.println("id: " + id.toString());
            e.printStackTrace();
        }
        try{
            if (actorStatement!=null)
                actorStatement.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void insertStarsInMovies(Connection conn)
    {
        Set mv_star_set = add_to_star_in_mv.entrySet();
        Iterator mv_star_list = mv_star_set.iterator();

        PreparedStatement mv_star_Statement = null;
        String actorQuery ="INSERT INTO  stars_in_movies (starId, movieId) VALUES (?,?);" ;
        int[] iNoRows=null;
        byte[] id = null;
        System.out.println("start printing star in movie list");
        Integer num=0;
        try
        {

            mv_star_Statement = conn.prepareStatement(actorQuery);

            while(mv_star_list.hasNext())
            {
                System.out.println(num);
                num++;
                Map.Entry mv_star = (Map.Entry)mv_star_list.next();
                System.out.println(mv_star.getValue().toString() + "  " +  mv_star.getKey().toString());

                mv_star_Statement.setString(1, mv_star.getValue().toString());
                mv_star_Statement.setString(2, mv_star.getKey().toString());

                mv_star_Statement.addBatch();


            }
            iNoRows = mv_star_Statement.executeBatch();
            System.out.println("Offically insert all the stars in movie from xml files!");

        }catch (Exception e)
        {
            //System.out.println("id: " + id.toString());
            e.printStackTrace();
        }
        try{
            if (mv_star_Statement!=null)
                mv_star_Statement.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void insertGenresInMovies(Connection conn, Iterator<Movie> movieIterator)
    {
        String sqlGenreIdQuery = "SELECT id FROM genres WHERE name= ? ";
        String sqlGenreMovieQuery = "INSERT INTO genres_in_movies (genreId, movieId)\n" +
                "  SELECT * FROM (SELECT ?, ?) AS tmp\n" +
                "WHERE NOT EXISTS(\n" +
                "  SELECT genreId, movieId FROM genres_in_movies WHERE genreId=? AND movieId=?\n" +
                ") LIMIT 1;";
        PreparedStatement psGenreMovieRecord = null;
        PreparedStatement psGenreIdRecord = null;
        try {
            psGenreMovieRecord = conn.prepareStatement(sqlGenreMovieQuery);
            psGenreIdRecord = conn.prepareStatement(sqlGenreIdQuery);
            while (movieIterator.hasNext()) {
                Movie movie = movieIterator.next();
                String movieId = movie.getId();
                ArrayList<String> genres = movie.getGenres();
                for (String genre : genres) {
                    // get genre id
                    psGenreIdRecord.setString(1, genre);
                    ResultSet rs = psGenreIdRecord.executeQuery();
                    rs.next();
                    int genreId = rs.getInt("id");
                    rs.close();
                    // insert into genres_in_movies table
                    psGenreMovieRecord.setInt(1, genreId);
                    psGenreMovieRecord.setString(2, movieId);
                    psGenreMovieRecord.setInt(3, genreId);
                    psGenreMovieRecord.setString(4, movieId);
                    psGenreMovieRecord.addBatch();
                }
            }
            psGenreMovieRecord.executeBatch();

            psGenreIdRecord.close();
            psGenreMovieRecord.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void insertGenres(Connection conn, ArrayList<String> genres)
    {
        // execute after movie are inserted into the genres
        String sqlGenreInsertQuery = "INSERT INTO genres (name)\n" +
                "  SELECT * FROM (SELECT ?) AS tmp\n" +
                "  WHERE NOT EXISTS (\n" +
                "      SELECT name FROM genres WHERE name = ?\n" +
                "  ) LIMIT 1;";

        PreparedStatement psGenreInsertRecord = null;
        try
        {
            psGenreInsertRecord = conn.prepareStatement(sqlGenreInsertQuery);

            for(String genre: genres) {
                // add genre if not exists
                psGenreInsertRecord.setString(1, genre);
                psGenreInsertRecord.setString(2, genre);
                psGenreInsertRecord.executeUpdate();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try{
            if (psGenreInsertRecord!=null) psGenreInsertRecord.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void insertMovies(Connection conn, String movieXMLpath)
    {
        parse_movie parser = new parse_movie();
        List<Movie> movieList =  parser.getMovies(movieXMLpath);
        Iterator<Movie>  movieIterator =movieList.iterator();
        PreparedStatement psInsertRecord = null;
        //tt0421974','Sky Fighters',2005,'Gérard Pirès'
        String sqlInsertRecord="INSERT IGNORE INTO movies VALUES(?,?,?,?);";

        int[] iNoRows=null;
        try
        {

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);
//            psGenreInsertRecord=conn.prepareStatement(sqlGenreInsertRecord);
            while (movieIterator.hasNext())
            {
                Movie movie = movieIterator.next();
                psInsertRecord.setString(1, movie.getId());
                psInsertRecord.setString(2, movie.getTitle());
                psInsertRecord.setInt(3, movie.getYear());
                psInsertRecord.setString(4, movie.getDirector());
                // TODO: genre related
                insertGenres(conn, movie.getGenres());
                psInsertRecord.addBatch();
            }
            iNoRows=psInsertRecord.executeBatch();
            insertGenresInMovies(conn, movieList.iterator());





        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//        System.out.println("hello world");

        Connection conn = null;
        String moviePath = args[0];
        String actorPath = args[1];
        String castPath = args[2];
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb?useSSL=false";
        try {
            conn = DriverManager.getConnection(jdbcURL,"jenny", "cs122b");
            conn.setAutoCommit(false);
            getMaxStarId_MovieId(conn);
            System.out.println("Start loading movie and genre!");
            insertMovies(conn, moviePath);
            conn.commit();
            conn.setAutoCommit(false);
            //stars
            //first get all the data from database, then from xml file. Then insert the new one back
            System.out.println("Start loading stars and movie-star !");
            load_all_movie_id(conn);
            load_mv_star_from_db(conn);
            load_star_name_id_pairs(conn);
            load_parse_actor(conn,actorPath);
            add_parse_cast(conn, castPath);
            insertStars(conn);
            conn.commit();
            insertStarsInMovies(conn);
            conn.commit();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
