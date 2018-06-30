DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb DEFAULT CHARACTER SET utf8;
use moviedb;

CREATE TABLE movies (
    id VARCHAR(10) NOT NULL,
    title VARCHAR(100) NOT NULL DEFAULT '',
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL DEFAULT '',
    PRIMARY KEY (id),
    FULLTEXT (title)
);

CREATE TABLE stars(
	id VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL DEFAULT '',
    birthYear INTEGER DEFAULT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE stars_in_movies(
	starId VARCHAR(10) NOT NULL,
    movieId VARCHAR (10) NOT NULL,
    PRIMARY KEY (starId, movieId),
	FOREIGN KEY (starId) REFERENCES stars (id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies (id) ON DELETE CASCADE
);

CREATE TABLE genres(
	id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL DEFAULT '',
    PRIMARY KEY (id)
);


CREATE TABLE genres_in_movies (
	genreId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    PRIMARY KEY (genreId, movieId),
    FOREIGN KEY (genreId) REFERENCES genres (id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies (id) ON DELETE CASCADE
);


CREATE TABLE creditcards (
	id VARCHAR(20) NOT NULL,
    firstName VARCHAR (50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    expiration DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE customers (
	id INTEGER NOT NULL AUTO_INCREMENT,
    firstName VARCHAR (50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    ccId VARCHAR(20) NOT NULL DEFAULT '',
    address VARCHAR (200) NOT NULL DEFAULT '',
    email VARCHAR(50) NOT NULL DEFAULT '' UNIQUE,
    password VARCHAR(20) NOT NULL DEFAULT '',
    PRIMARY KEY (id),
    --  Q: when credit card is deleted, customer should not be deleted
    FOREIGN KEY (ccId) REFERENCES creditcards(id) ON UPDATE CASCADE 
);

CREATE TABLE sales(
	id INTEGER   NOT NULL AUTO_INCREMENT,
    customerId INTEGER NOT NULL,
    movieId VARCHAR (10) NOT NULL,
    saleDate DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES customers (id),
    FOREIGN KEY (movieId) REFERENCES movies (id) 
);

CREATE TABLE ratings (
	movieId VARCHAR(10)  NOT NULL,
    rating FLOAT NOT NULL,
    numVotes INTEGER  NOT NULL,
    PRIMARY KEY (movieId),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);


CREATE TABLE  employees
 (
email varchar(50) not null,
password varchar(20) not null,
fullname varchar(100),
primary key (email)
);
INSERT INTO employees VALUES('classta@email.edu','classta','TA CS122B');






