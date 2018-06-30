//cast124.xml


import java.io.IOException;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

//f is fid / Movie -id
//a stagename / actor name

public class parse_cast {


    public HashMap<String, ArrayList<String>> myMovie_Actors;
    Document dom;

    public parse_cast() {
        //create a list to hold the movieIds and actors' name
        myMovie_Actors = new HashMap< String, ArrayList<String> >();
    }

    public HashMap<String, ArrayList<String>> getMyMovie_Actors(String path)
    {
        //parse the xml file and get the dom object
        parseXmlFile(path);

        //get each employee element and create a Employee object
        parseDocument();
        return myMovie_Actors;
    }
//    public void runExample() {
//
//        //parse the xml file and get the dom object
//        parseXmlFile();
//
//        //get each employee element and create a Employee object
//        parseDocument();
//
//        //Iterate through the list and print the data
//        printData();
//
//    }

    private void parseXmlFile(String path) {

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(path);

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
        //one filmc corresponding to one film, and each <m><a> is one actor
        NodeList nl = docEle.getElementsByTagName("filmc");
        if (nl != null && nl.getLength() > 0) {
            //System.out.println("nl length is:" + nl.getLength());
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);
                //same Movie -- different actors

                //loop all the actors, should share same Movie id
                NodeList actor_list = el.getElementsByTagName("m");
                if (actor_list != null && actor_list.getLength() > 0) {
                    String movieId = getTextValue(el,"f" );
                    ArrayList<String> actors_list = new ArrayList<String>();

                    for (int j = 0; j < actor_list.getLength(); j++) {
                        Element actor = (Element) actor_list.item(j);
                        String actor_name = getTextValue(actor,"a");
                        actors_list.add(actor_name);

                    }

                    if ( !myMovie_Actors.containsKey(movieId) )
                    {
                        myMovie_Actors.put(movieId,actors_list);
                    }
                    else
                    {
                        myMovie_Actors.get(movieId).addAll(actors_list);
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


    private void printData() {

        System.out.println("No of movies '" + myMovie_Actors.size() + "'.");
        Set movie_actor_set = myMovie_Actors.entrySet();
        Iterator movie_actor_iter = movie_actor_set.iterator();
        while (movie_actor_iter.hasNext()) {
            Map.Entry movie_actor = (Map.Entry) movie_actor_iter.next();

            System.out.println(movie_actor.getKey()  + " "+ movie_actor.getValue().toString());
        }
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        PrintStream fileStream = new PrintStream("stanford-movies/parse_cast.txt");
//        System.setOut(fileStream);
//
//        //create an instance
//        parse_cast dpe = new parse_cast();
//
//        //call run example
////        dpe.runExample();
//    }

}


