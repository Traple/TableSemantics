import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//This object contains a column of a table.
//This column will read itself from the XML output file.
public class Column {

    private ArrayList<String> content = new ArrayList<String>();
    private double similarityThreshold = 0.90;

    /**
     * This constructor creates a new column by using a TEA input file.
     * @param inputFile one of the TEA output files (XML)
     * @param index which column this object is (first, second etc.)
     * @throws ParserConfigurationException if we cant parse the XML
     * @throws SAXException when there is something wrong with the API
     * @throws XPathExpressionException if there is something wrong with the xml file
     * @throws IOException if the program cant find the file
     */
    public Column(String inputFile, int index) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        readColumn(inputFile, index);
        contentNorm();
    }

    /**
     * This constructor creates a new column by using an arraylist containing the content of the column.
     * @param content
     */
    public Column(ArrayList<String> content){
        this.content = content;
        contentNorm();
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
        Collections.addAll(content, columnInArray);
        this.content = content;
    }

    /**
     * This method normalizes the content so it is easier to compare it with the query. This includes:
     * Glyph conversion
     * Capital letter conversion
     */
    private void contentNorm(){
        ArrayList<String> oldContent = this.content;
        ArrayList<String> newContent = new ArrayList<String>();
        for(String cell : oldContent){
            cell = cell.toLowerCase();
            if(cell.contains("ﬁ")){
                cell = cell.replace("ﬁ", "fi");
            }
            newContent.add(cell);
        }
        this.content = newContent;
    }

    //To compare the query against this column.
    //We want to keep the first two cells for the purpose of having a single header
    public boolean containsHeader(String header){
        boolean containsHeader = false;
        String firstWord = "";
        String secondWord = "";
        for(String word : content){
            if(content.get(0).equals(word)){
                firstWord = word;
            }
            if(word.contains(header)){
                containsHeader = true;
                break;
            }
            else if(content.size() > 1 && content.get(1).equals(word)){
                secondWord = word;
                if((firstWord+secondWord).equals(header)|| (firstWord + " " + word).equals(header)){
                    containsHeader = true;
                    break;
                }
            }
            else if(content.size() >2 && content.get(2).equals(word) && ((firstWord+secondWord+word).equals(header) ||
                    (firstWord + " " + secondWord + " " + word).equals(header))){
                containsHeader = true;
                break;
            }
            else if(content.size() >2 && content.get(2).equals(word) && ((secondWord+word).equals(header) ||
                    (secondWord + " " + word).equals(header))){
                containsHeader = true;
                break;
            }
        }
        return containsHeader;
    }




    /**
     * Loop the same way as contain but use the JaroWrinkler class to validate the similarity.
     * ASSUMES that the multiple word detection already picked up combination of words. Can be improved according to test results.
     * @param header A header to be tested for mapping.
     * @return a boolean that returns true if the JaroWrinkler algoritm returns positive.
     */
    public boolean mightContainHeader(String header){
        boolean containsHeader = false;
        String firstWord = "";
        String secondWord = "";
        for(String word : content){
            if(content.get(0).equals(word)){
                firstWord = word;
            }
            if(JaroWinkler.compare(header, word) > similarityThreshold){
                containsHeader = true;
                break;
            }
            else if(content.size() > 1 && content.get(1).equals(word)){
                secondWord = word;
                if(JaroWinkler.compare((firstWord+secondWord),header)>similarityThreshold ||
                        JaroWinkler.compare((firstWord+" "+secondWord),header)>similarityThreshold){
                    containsHeader = true;
                    break;
                }
            }
            else if(content.size() >2 && content.get(2).equals(word) && JaroWinkler.compare((firstWord+secondWord+word),header)>similarityThreshold
                    || JaroWinkler.compare((firstWord+" "+secondWord+" "+word),header)>similarityThreshold){
                containsHeader = true;
                break;
            }
            else if(content.size() >2 && content.get(2).equals(word) && JaroWinkler.compare((secondWord+" "+word),header)>similarityThreshold ||
                    JaroWinkler.compare((secondWord+word),header)>similarityThreshold){
                containsHeader = true;
                break;
            }
        }
        return containsHeader;
    }

    //This toString method returns the current content of the column.
    public String toString(){
        return content.toString();
    }

    public ArrayList<String> getContent() {
        return content;
    }

    //if the column need to change its content (Only used by the columnSwap used in the HeaderColumn.
    public void setContent(ArrayList<String> content) {
        this.content = content;
    }
}
