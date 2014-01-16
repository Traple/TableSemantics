import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        //read the XML files
        //Create general vector map
        //Find similarity per vector
        //List files on similarity
        //Output the results

        String workspace = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources\\results";
//        String workspace = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7RandomCorpus2-2\\resources\\results";
//        ArrayList<String> title = new ArrayList<String>(Arrays.asList("Comparison","between","copper","substituted","cobalt", "substituted","and","native","yeast", "ADH"));
        ArrayList<String> title = new ArrayList<String>(Arrays.asList("Puriﬁcation","Purification","purification"));
        ArrayList<String> headers = new ArrayList<String>(Arrays.asList("Purification","Puriﬁcation","Purification" , "purification", "Yield", "Yield", "yield", "Step", "step", "fold"));
        //Interesting Queries:
        //"Substrate","Substrate" , "substrate", "Compounds", "Compounds","compounds","Relative", "Relative", "relative","activity"
        ArrayList<Table> tables = readXMLFiles(workspace);
        VectorMap vectorMap = new VectorMap(tables);
        System.out.println(vectorMap);

        Query query = new Query(title,headers,vectorMap.getTitleMap(),vectorMap.getHeaderMap());
        System.out.println("Now with query: ");
        System.out.println(query);
        System.out.println("Now we see if we can do something new: ");
        for(Table table :VectorMap.sortByValue(query.rankTableHeaders(tables)).keySet()){
            System.out.println(table + " " + VectorMap.sortByValue(query.rankTableHeaders(tables)).get(table));
        }
        System.out.println("Titles: ");
        for(Table table : VectorMap.sortByValue(query.rankTableTitles(tables)).keySet()){
            System.out.println(table + " " + VectorMap.sortByValue(query.rankTableTitles(tables)).get(table));
        }
    }

    /**
     * This method finds the XML files (using the findXMLs method) and read their properties. These are stored in the Table class.
     * @param workspace The workspace that was used during the run.
     */
    public static ArrayList<Table> readXMLFiles(String workspace) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        ArrayList<String> XMLFiles = findXMLs(workspace);
        ArrayList<Table> tables = new ArrayList<Table>();
        for(String XMLFile : XMLFiles){
            Table currentTable = new Table(XMLFile);
            tables.add(currentTable);

        }
        return tables;
    }

    public static ArrayList<String> findXMLs(String workspace){
        ArrayList<String> XMLFiles = new ArrayList<String>();
        File dir = new File(workspace);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });
        for(File file : files){
            XMLFiles.add(file.getAbsolutePath());
        }
        System.out.println(XMLFiles);
        return XMLFiles;
    }
}
