import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

//Interesting Queries:
//"Purification","Puriﬁcation", "purification", "Yield", "yield", "Step", "step", "fold", "Total activity","total activity", "Total protein", "total protein", "activity","protein"
//"Substrate","Substrate" , "substrate", "Compounds", "Compounds","compounds","Relative", "Relative", "relative","activity"

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String workspace = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7RandomCorpus2\\resources\\results";

        //Explanation of the Query:
        //title: Only used for the detection of relevant papers
        //headers: Only used for the detection of relevant papers
        //headers IS NOT ALLOWED TO CONTAIN MULTIPLE WORDS.
        //headers2: Used for mapping the information

          ArrayList<String> title = new ArrayList<String>(Arrays.asList("loci", "combination"));
          ArrayList<String> headers = new ArrayList<String>(Arrays.asList("loci", "patterns", "variable"));
          ArrayList<String> headers2 = new ArrayList<String>(Arrays.asList("loci", "isolates", "variable"));

        ArrayList<Table> tables = readXMLFiles(workspace);
        VectorMap vectorMap = new VectorMap(tables);

        Query query = new Query(title,headers,vectorMap.getTitleMap(),vectorMap.getHeaderMap());
        System.out.println(query);
        //Filter out the tables that have a relevance score < 2

        Map<Table, Integer> headerVectorMap = new HashMap<Table, Integer>();

        for(Table table :VectorMap.sortByValue(query.rankTableHeaders(tables)).keySet()){
            if(VectorMap.sortByValue(query.rankTableHeaders(tables)).get(table)>-1){
                headerVectorMap.put(table, VectorMap.sortByValue(query.rankTableHeaders(tables)).get(table));
            }
        }

        Map<Table, Integer> titleVectorMap = new HashMap<Table, Integer>();
        for(Table table :VectorMap.sortByValue(query.rankTableTitles(tables)).keySet()){
            if(VectorMap.sortByValue(query.rankTableTitles(tables)).get(table)>-1){
                titleVectorMap.put(table, VectorMap.sortByValue(query.rankTableTitles(tables)).get(table));
            }
        }

        //Todo: combine the title and header scores in a single relevance vector.
        Map<Table, Integer> relevanceVectorMap = new HashMap<Table, Integer>();

        for(Table table : headerVectorMap.keySet()){
            int relevanceScore;
            if(titleVectorMap.containsKey(table)){
                relevanceScore = titleVectorMap.get(table) + headerVectorMap.get(table);
                relevanceVectorMap.put(table,relevanceScore);
            }
            else{
                relevanceVectorMap.put(table, headerVectorMap.get(table));
            }
        }
        for(Table table : titleVectorMap.keySet()){
            if(!relevanceVectorMap.containsKey(table)){
                relevanceVectorMap.put(table, titleVectorMap.get(table));
            }
        }
        relevanceVectorMap = VectorMap.sortByValue(relevanceVectorMap);


        System.out.println("Only the juicy stuff, we like low hanging fruit: ");
        System.out.println(relevanceVectorMap);

        for(Table table : relevanceVectorMap.keySet()){
            System.out.println(table);
            System.out.println(table.getMappedColumns(headers2));
        }
    }

    /**
     * This method finds the XML files (using the findXMLs method) and read their properties. These are stored in the Table class.
     * @param workspace The workspace that was used during the run.
     */
    public static ArrayList<Table> readXMLFiles(String workspace) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        System.out.println("Reading tables");
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
