package task1;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Клас для читання XML-файлів та їх копіювання зі змінами.
 */
public class XMLParser {
    private static final String FILE = "src/main/resources/task1/input.xml"; //Path of input file

    //Function to read the tag
    public static void readTag() {

        //Create factory for document builder creation
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //Create file writer
        FileWriter fw = null;

        try {

            DocumentBuilder db = dbf.newDocumentBuilder(); //Create document builder
            Document document = db.parse(FILE); //Build document

            NodeList nodeList = document.getElementsByTagName("person"); //Get tag list

            //If we don't have such tags - finish the program
            if (nodeList.getLength() == 0) {
                System.out.println("Document don't have any tag \"person\".");
                return;
            }

            //Initialize file writer with output file path
            fw = new FileWriter("src/main/resources/task1/output.xml");

            //Write open tag
            fw.write("<persons>\n");

            //Write all elements of persons
            for (int i = 0; i < nodeList.getLength(); i++) {
                writeElement(nodeList, i, fw); //Write each element
            }

            //Write end tag
            fw.write("</persons>\n");

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {

                    //Close streams
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Function to write element to file copy
    private static void writeElement(NodeList nodeList, int i, FileWriter fw) {
        Node node = nodeList.item(i); //Create each node

        //If node is an element
        if (node.getNodeType() == Node.ELEMENT_NODE) {

            Element element = (Element) node; //Create element from node


            String surname = element.getAttribute("surname"); //Get surname
            String name = element.getAttribute("name") + " " + surname; //Get name

            element.removeAttribute("surname"); //Remove surname from tag "person"

            element.removeAttribute("name"); //Remove name from tag "person"
            element.setAttribute("name", name); //Set new attribute name in tag "person"

            NamedNodeMap attributes = element.getAttributes(); //Get list of attributes

            //Write each tag in "persons"
            writeTag(fw, attributes);
        }
    }

    //Function write tag to file copy
    private static void writeTag(FileWriter fw, NamedNodeMap attributes) {

        try {

            //Write opening of tag
            fw.write("\t<person ");

            //Write all attributes of tag
            for (int i = 0; i < attributes.getLength(); i++) {
                fw.write(attributes.item(i) + " "); //Write each attribute
                fw.flush(); //Clear file writer
            }

            //Write end of the tag
            fw.write("/>\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
