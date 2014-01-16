import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;

//This object contains a column of a table.
//This column will read itself from the XML output file.
public class Column {

    private ArrayList<String> content = new ArrayList<String>();

    public Column(String inputFile, int index) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        readColumn(inputFile, index);
    }
    private void readColumn(String inputFile, int index) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        ArrayList<String> content = new ArrayList<String>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        org.w3c.dom.Document doc;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(String.valueOf(inputFile));

        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpath = xpathFactory.newXPath();


        String expression = "(/TEAFile/results/columns/column)["+index+"]";
        String columnInFile;

        XPathExpression expr =
                xpath.compile(expression);
        columnInFile = (String) expr.evaluate(doc, XPathConstants.STRING);
        String[] columnInArray = columnInFile.split(", ");
        for(String word  : columnInArray){
            content.add(word);
        }
        this.content = content;
    }

    //To compare the query against this column.
    public boolean containsHeader(String header){
        boolean containsHeader = false;
/*        if(header.contains(" ")){
            System.out.println("Breaking The Query");
            boolean containsAPartOfHeader= false;
            String[] headersArray = header.split(" ");
            ArrayList<String> headers = new ArrayList<String>();
            for(String word : headersArray){
                headers.add(word);
            }
            System.out.println(headers) ;
            Header : for(String word : headers){
                Content : for(String cell : content){
                    if(cell.contains(word)&& containsAPartOfHeader){
                        System.out.println(cell + " Overdrive! " + word);
                        containsHeader = true;
                        break Header;
                    }
                    else if(cell.contains(word)){
                        containsAPartOfHeader = true;
                        System.out.println(cell + " got hit by: " + word);
                        continue Header;
                    }
                }
            }
        }*/
        if(!containsHeader){
            for(String word : content){
                if(word.contains(header)){
                    containsHeader = true;
                }
            }
        }
        return containsHeader;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public String toString(){
        String string = content.toString();
        return string;
    }
}
