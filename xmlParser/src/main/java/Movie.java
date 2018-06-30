import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private Integer year;
    private String director;
    private ArrayList<String> genres;

    public Movie() {

    }
    public Movie(String id, String title, Integer year, String director, ArrayList<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
    }
    public boolean isValid()
    {
        return id!=null & title!=null & year!=null & director!=null & genres!=null;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }
    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getYear() { return this.year; }
    public void setYear(int year) {
        this.year = year;
    }
    public String getDirector() { return director; }
    public void setDirector(String director) {
        this.director = director;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie [ ");
        sb.append("id: " + getId());
        sb.append(", ");
        sb.append("title: " + getTitle());
        sb.append(", ");
        sb.append("year: " + getYear());
        sb.append(", ");
        sb.append("director: " + getDirector());
        sb.append(", ");
        sb.append("genres: " + getGenres().toString());
        sb.append(" ]");

        return sb.toString();

    }
}

