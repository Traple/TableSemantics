import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;


/*TODO: Extend the table class so it will take all the information from the output file.
  We need to make sure that the information is being kept close. Link relevant headers to relevant titles to
  (Therefore) relevant cells.     */

public class Table {
    private String ID;
    private ArrayList<String> title;
    private ArrayList<String> headers;

    public Table(String XMLFile) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        readXMLFile(XMLFile);
    }

    private void readXMLFile(String XMLFile) throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        org.w3c.dom.Document doc;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(String.valueOf(XMLFile));

        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpath = xpathFactory.newXPath();
        String header;

        XPathExpression expr =
                xpath.compile("/TEAFile/tableSemantics/headers");
        header = (String) expr.evaluate(doc, XPathConstants.STRING);
        header = header.replace("[", "");
        header = header.replace("]", "");
        String[] headers = header.split(" ");

        ArrayList<String> headersList = new ArrayList<String>();
        for (String name : headers) {
            if (!name.contains(",")) {
                headersList.add(name);
            }
        }
        this.headers = headersList;

        xpath = xpathFactory.newXPath();
        String title;

        XPathExpression expr2 =
                xpath.compile("/TEAFile/tableSemantics/title");
        title = (String) expr2.evaluate(doc, XPathConstants.STRING);
        title = title.replace("[", "");
        title = title.replace("]", "");
        String[] titles = title.split(" ");

        ArrayList<String> titlesList = new ArrayList<String>();
        for (String name1 : titles) {
            if (!name1.contains(",")) {
                titlesList.add(name1);
            }
        }
        this.title = titlesList;

        xpath = xpathFactory.newXPath();
        String ID;

        XPathExpression expr3 =
                xpath.compile("/TEAFile/provenance/fromFile");
        ID = (String) expr3.evaluate(doc, XPathConstants.STRING);
        this.ID = ID;
    }

    public ArrayList<String> getHeaders(){
        return headers;
    }

    public ArrayList<String> getTitle(){
        return title;
    }

    public String getID() {
        return ID;
    }

    /**
     * Every table will identify itself using its ID.
     * @return A string containing the ID of the table.

     */
    public String toString(){
        String string = ID;
        return string;
    }
}
