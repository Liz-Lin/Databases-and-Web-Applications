INSERT INTO customers VALUES(961, 'a', 'a', '941', 'a1', 'a@email.com', 'a2');
INSERT INTO movies VALUES('id1','titleaaa1',2005,'dir1');
INSERT INTO movies VALUES('id2','titlebbc2',2005,'dir2');
INSERT INTO movies VALUES('id3','titleddd3',2005,'dir3');
INSERT INTO movies VALUES('id4','titlejkaa4',2005,'dir4');
INSERT INTO movies VALUES('id5','titlebc5',2005,'dir5');
INSERT INTO movies VALUES('id6','titlejk1',2005,'dir6');
INSERT INTO movies VALUES('id7','titlea and titleb',2005,'dir7');
INSERT INTO movies VALUES('id8','just title',2005,'dir6');
INSERT INTO stars (id, name, birthYear) VALUES('nm0000001','star1',1899);
INSERT INTO stars (id, name, birthYear) VALUES('nm0000002','star2abc d',1899);
INSERT INTO stars (id, name, birthYear) VALUES('nm0000003','star 3 ef',1899);
INSERT INTO stars_in_movies VALUES('nm0000001','id1');
INSERT INTO stars_in_movies VALUES('nm0000002','id2');
INSERT INTO stars_in_movies VALUES('nm0000003','id3');
INSERT INTO stars_in_movies VALUES('nm0000001','id2');
INSERT INTO stars_in_movies VALUES('nm0000002','id4');


SELECT * FROM movies;