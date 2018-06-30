
use moviedb;
DROP PROCEDURE IF EXISTS add_movie;
DELIMITER $$
CREATE PROCEDURE add_movie
(IN m_title VARCHAR(100),
IN m_year INT(11),
IN m_director VARCHAR(100),
IN star_name VARCHAR(100),
IN s_year INT(11),
IN genre_name VARCHAR(100), 
OUT message VARCHAR(500))

BEGIN
	DECLARE movieID  VARCHAR(10) DEFAULT NULL;
	DECLARE starID VARCHAR(10) DEFAULT NULL;
	DECLARE genreID INT(11) DEFAULT NULL;


	DECLARE m_id_int INT DEFAULT 0;
	DECLARE s_id_int INT DEFAULT 0;
	DECLARE g_id_int INT DEFAULT 0;

	DECLARE movie_star INT DEFAULT 0;
	DECLARE movie_genre INT DEFAULT 0;

	IF m_title = "" OR m_year = 0 OR m_director = "" OR star_name ="" OR s_year = "" OR genre_name = "" 
	THEN	
		SET message = "One of the parameter is null \n";
	ELSE
		SET message ="";
		SET movieID = (SELECT id FROM movies m WHERE m.title = m_title AND m.year = m_year AND m.director = m_director);
		SET starID =  (SELECT id FROM stars s WHERE star_name = s.name AND s_year = s.birthYear);
		SET genreID = (SELECT id FROM genres g WHERE  g.name = genre_name );
	

		SELECT mID INTO m_id_int FROM IDS;
		SELECT sID INTO s_id_int FROM IDS;
		SELECT gID INTO g_id_int FROM IDS;

		#check if movie exist: 
		IF movieID IS NULL 
		THEN
			SET movieID = concat("XXXX",CAST(m_id_int AS CHAR(10)));
			INSERT INTO movies (id, title, year, director) VALUES(movieID,m_title, m_year, m_director);
			UPDATE IDS SET  mID = m_id_int +1;
			
			SET message = CONCAT(message,"Movie Inserted & IDS Updated!\n");
		ELSE
			SET message = CONCAT(message, "Movie Already Exist!\n");
		END IF;

		#check if star exists
		IF starID IS NOT NULL 
		THEN 
			SET message = CONCAT(message, "Star Already Exist!\n");
			SELECT  COUNT(*) INTO movie_star FROM stars_in_movies S WHERE S.movieId = movieID AND S.starId = starID;
			#STILL NEED TO CHECK IF THIS RELATION ALREADY EXIST
			IF movie_star =0 
			THEN
				INSERT INTO stars_in_movies (starId, movieId)VALUES(starID, movieID);
			SET message = CONCAT(message, "Movie-Star Relation Inserted!\n");

			ELSE
				SET message = CONCAT(message, "Movie-Star Relation Already Exist!\n");
			END IF;

		ELSE
			SET starID = concat("nm",CAST(s_id_int AS CHAR(10)));
			INSERT INTO stars (id,name, birthYear) VALUES(starID, star_name,s_year);

			UPDATE IDS  SET sID = s_id_int + 1;
			INSERT INTO stars_in_movies (starId, movieId) VALUES(starID, movieID);
			SET message = CONCAT(message,"Star Inserted & Movie-Star Relation Inserted & IDS Updated!\n");
		END IF;

		#check if genre exist:
		IF genreID IS NOT NULL 
		THEN
			SET message = CONCAT(message, "Genre Already Exist!\n");
			SELECT COUNT(*) INTO movie_genre FROM genres_in_movies G WHERE G.movieId = movieID AND G.genreId = genreID;
			#STILL NEED TO CHECK IF THIS RELATION ALREADY EXIST
			IF movie_genre=0 
			THEN
				INSERT INTO genres_in_movies(genreId, movieId) VALUES(genreID, movieID);
				SET message = CONCAT(message, "Movie-Genre Relation Inserted!\n");
			ELSE
				SET message = CONCAT(message, "Movie-Genre Relation Already Exist!\n");
			END IF;
		
		ELSE
			#INSERT INTO genres VALUES(27,'new genre5');
            SET genreID =g_id_int;
			INSERT INTO genres (id,name) VALUES(genreID, genre_name);
			UPDATE IDS SET gID = g_id_intid + 1;
			INSERT INTO genres_in_movies (genreId, movieId)  VALUES(genreID, movieID);
			SET message = CONCAT(message,"Genre Inserted & Movie-Genre Relation Inserted & IDS Updated!\n");
		END IF;

	
	#get rid of
	END IF;
END
$$

DELIMITER ;


