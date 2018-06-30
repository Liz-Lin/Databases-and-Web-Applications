package edu.uci.ics.team43.fabflix;

public class Movie {

    private String id;
    private String title;
    private String year;
    private String director;
    private String stars;
    private String genres;
    private String rating;



    public Movie(String id, String title, String year, String director, String stars, String genres, String rating) {
        this.id = id;
        this.title=title;
        this.year=year;
        this.director = director;
        this.stars=stars;
        this.genres=genres;
        this.rating=rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {

        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getStars() {
        return stars;
    }

    public String getGenres() {
        return genres;
    }

    public String getRating() {
        return rating;
    }

}
