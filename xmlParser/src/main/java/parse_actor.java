//actor63.xml

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


public class parse_actor {

    private List<Actor> myActors;
    Document dom;

    public parse_actor() {
        //create a list to hold the Movie objects
        myActors = new ArrayList<Actor>();
    }
    public List<Actor> getActors(String xmlPath)
    {
        myActors.clear();
        parseXmlFile(xmlPath);
        parseDocument();
        return myActors;
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
////ID, NAME -- stagename, BIRTH YEAR -- dob
    private void parseDocument() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <Movie> elements
        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0) {

            System.out.println("nl length is: "+ nl.getLength());
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);//single actor
                String name = getTextValue(el, "stagename");
                Integer year = getIntValue(el, "dob");
                Actor star = new Actor(null, name, year);
                myActors.add(star);
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

        System.out.println("No of actors '" + myActors.size() + "'.");

        Iterator<Actor> it = myActors.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public void runExample(String xmlPath) {

        //parse the xml file and get the dom object
        parseXmlFile(xmlPath);

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }
//    public static void main(String[] args) throws FileNotFoundException {
//        PrintStream fileStream = new PrintStream("parse_actor.txt");
//        System.setOut(fileStream);
//
//
//        //create an instance
//        parse_actor dpe = new parse_actor();
//
//        //call run example
//        dpe.runExample("stanford-movies/actors63.xml");
//    }

}





