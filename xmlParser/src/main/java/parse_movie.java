import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class parse_movie {

    private List<Movie> myMovies;
    Document dom;
    private String movieXMLpath;

    public List<Movie> getMovies(String movieXMLpath)
    {
        myMovies.clear();
        parseXmlFile(movieXMLpath);
        parseDocument();
        return myMovies;
    }
    public parse_movie() {
        //create a list to hold the Movie objects
        myMovies = new ArrayList<Movie>();
    }

    private void runExample(String xmlPath) {

        //parse the xml file and get the dom object
        parseXmlFile(xmlPath);

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile(String xmlPath) {

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(xmlPath);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

    private void parseDocument() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <Movie> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                //contains multiple films directed by same director
                String director = getTextValue(el,"dirname" );

                NodeList film_list = el.getElementsByTagName("film");
                
                if (film_list != null && film_list.getLength() > 0) {
                    for (int j = 0; j < film_list.getLength(); j++) {
                        Element filmEl = (Element) film_list.item(j);
                        Movie Movie = getMovie(filmEl, director);
                        if (Movie.isValid()) myMovies.add(Movie);
                    }
                }

            }
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            //textVal = el.getFirstChild().getNodeValue();
            textVal = el.getTextContent();
        }

        return textVal;
    }

    private Integer getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        }
        catch (NumberFormatException nfe)
        {
            return null;
        }
    }


    //ID TITLE YEAR DIRECTOR GENRES
    private Movie getMovie(Element mvEl, String current_director) {
        String id = getTextValue(mvEl, "fid");
        String title = getTextValue(mvEl, "t");
        Integer year = getIntValue(mvEl, "year");
        NodeList genre_list = mvEl.getElementsByTagName("cat");
        ArrayList<String> genre_name_list = new ArrayList<String>();

        if (genre_list != null && genre_list.getLength() > 0) {

            for (int j = 0; j < genre_list.getLength(); j++) {
                Element genre = (Element) genre_list.item(j);
                String genre_name = genre.getTextContent();
                genre_name_list.add(genre_name);
            }
        }

//public Movie(String id, String title, Integer year, String director,String genres) {

        Movie Movie = new Movie(id, title, year, current_director,genre_name_list);
        return Movie;
    }





    private void printData() {

        System.out.println("No of movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }
//
//    public static void main(String[] args) throws FileNotFoundException {
//        PrintStream fileStream = new PrintStream("stanford-movies/parse_movie.txt");
//        System.setOut(fileStream);
//
//
//        //create an instance
//        parse_movie dpe = new parse_movie();
//
//        //call run example
//        dpe.runExample("stanford-movies/mains243.xml");
//    }

}

