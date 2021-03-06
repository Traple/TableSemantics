import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;

public class Table {
    private String ID;
    private ArrayList<String> title;
    private ArrayList<String> headers;
    private ArrayList<Column> columns;
    private boolean isHeaderColumn;
    private String humanReadableMatches;
    private ArrayList<Column> significantUnmappedColumns;
    private String XMLFile;

    public Table(String XMLFile, ArrayList<String> queries, boolean supportHeaderColumns, int requiredHeadersInHeaderColumn) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        this.XMLFile = XMLFile;
        readXMLFile(XMLFile);
        extractColumns(XMLFile);
        if(supportHeaderColumns){
            HeaderColumn headerColumn = new HeaderColumn(columns, queries, requiredHeadersInHeaderColumn);
            if(headerColumn.containsHeaderColumn()){
                this.isHeaderColumn = true;
                columns = headerColumn.getColumns();

            }
            else{
                this.isHeaderColumn = false;
            }
        }
        else{
            this.isHeaderColumn = false;
        }
    }

    private void readXMLFile(String XMLFile) throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        org.w3c.dom.Document doc;
        builder = factory.newDocumentBuilder();

        System.out.println(XMLFile);
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
    private void extractColumns(String inputFile) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
        ArrayList<Column> columns = new ArrayList<Column>();

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
        String columnsInFile;

        XPathExpression expr =
                xpath.compile("/TEAFile/results/columns");
        columnsInFile = (String) expr.evaluate(doc, XPathConstants.STRING);

        int numberOfColumns= ESM.countOccurrences(columnsInFile, "\n");

        for(int index = 0; index<numberOfColumns;index++){
            Column currentColumn = new Column(inputFile, index);
            columns.add(currentColumn);
        }

        this.columns = columns;
    }

    /**
     * this method returns the columns from which the headers map to the query.
     * @param query A list containing the headers (query)
     * @return A list of columns that mapped against this query.
     */
    public ArrayList<Column> getMappedColumns(ArrayList<String> query){
        ArrayList<Column> mappedColumns = new ArrayList<Column>();
        ArrayList<Column> unmappedColumns = columns;
        boolean mappedAColumn = false;
        ArrayList<String> unmappedHeaders = new ArrayList<String>();
        Column mappedColumn = null;
        String lineSep = System.getProperty("line.separator");
        String humanReadableMatches = "";
        for(String headerQuery : query){
            String output = "";
            boolean headermapped = false;
            int matches = 0;
            if(mappedAColumn){
                unmappedColumns.remove(mappedColumn);
                mappedAColumn = false;
            }
            for(Column column : unmappedColumns){
                if(column.containsHeader(headerQuery)){
                    humanReadableMatches = humanReadableMatches + (headerQuery + " matches " + column) + lineSep;
                    mappedColumns.add(column);
                    mappedAColumn = true;
                    mappedColumn = column;
                    headermapped = true;
                    matches += 1;
                }
                else if(column.mightContainHeader(headerQuery)){
                    humanReadableMatches = humanReadableMatches + (headerQuery + " might match " + column) + lineSep;
                    mappedColumns.add(column);
                    matches+=1;
                }

            }
            if(!output.equals("")&&matches>1){
                output = "";
                matches = 0;
            }
            if(!headermapped){
                unmappedHeaders.add(headerQuery);
            }
        }
        if(unmappedColumns.size() == 1 && unmappedHeaders.size() == 1){
            this.significantUnmappedColumns = unmappedColumns;
        }

        this.humanReadableMatches = humanReadableMatches;
        return mappedColumns;
    }

    public ArrayList<String> getHeaders(){
        return headers;
    }

    public ArrayList<String> getTitle(){
        return title;
    }
    /**
     * Every table will identify itself using its ID.
     * @return A string containing the ID of the table.

     */
    public String toString(){
        return ID;
    }

    public String getHumanReadableMatches() {
        return humanReadableMatches;
    }
    public ArrayList<Column> getSignificantUnmappedColumns() {
        return significantUnmappedColumns;
    }
    public boolean isHeaderColumn (){
        return isHeaderColumn;
    }
    public String getXMLFile(){
        return XMLFile;
    }

}
