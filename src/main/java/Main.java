import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, ParseException {
        String workspace = "C:/Users/Sander van Boom/Documents/School/tables/ic50corpus/allResults";

        //Explanation of the Query:
        //title: Only used for the detection of relevant papers
        //headers: Only used for the detection of relevant papers
        //headers IS NOT ALLOWED TO CONTAIN MULTIPLE WORDS.
        //headers2: Used for mapping the information

        ArgumentProcessor arguments = new ArgumentProcessor(args);
        ArrayList<String> title = arguments.getTitle();
        ArrayList<String> headers = arguments.getHeaders();
        ArrayList<String> headers2 = arguments.getHeaders();
        boolean supportHeaderColumn = arguments.supportHeaderColumns();
        boolean iteration = arguments.supportIteration();

//        boolean supportHeaderColumn = true;
        int requiredHeadersInHeaderColumn = 2;
//        boolean iteration = true;
        int maxRunNumber = 3;
//        ArrayList<String> title = new ArrayList<String>(Arrays.asList("data", "collection", "refinement", "statistics"));
//        ArrayList<String> headers = new ArrayList<String>(Arrays.asList("space", "group", "resolution", "completeness", "unique", "reflections", "cell", "dimensions"));
//        ArrayList<String> headers2 = new ArrayList<String>(Arrays.asList("space group", "resolution", "completeness", "unique reflections","cell dimensions" ));


//        ArrayList<String> title = new ArrayList<String>(Arrays.asList("binding","assay"));
//        ArrayList<String> headers = new ArrayList<String>(Arrays.asList("ic50", "Peptide","sequence", "Residue","substituted"));
//        ArrayList<String> headers2 = new ArrayList<String>(Arrays.asList("ic50", "Peptide sequence", "Residue substituted"));

        ArrayList<Table> tables = readXMLFiles(workspace, headers2, supportHeaderColumn, requiredHeadersInHeaderColumn);
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


        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        //Header Mapping (TEA MAP)
        //TODO: Create an iteration process so unmapped columns can be used in the query if the table score is high enough also create appropriate thresholds and parameters.
        //TODO: Use another test set to prove that the iteration is possible.

        if(iteration){
            int runNumber = 0;
            String lineSep = System.getProperty("line.separator");
            while (true){
                String humanReadableOutput = "Current results of table mapping, run number: " + runNumber + lineSep;
                System.out.println("Only the juicy stuff, we like low hanging fruit: ");
                System.out.println(relevanceVectorMap);

                humanReadableOutput = humanReadableOutput + relevanceVectorMap + lineSep;
                for(Table table : relevanceVectorMap.keySet()){
                    if(table.isHeaderColumn()){
                        System.out.println("This Table is contains a headerColumn. Mapping may not be as accurate.");
                        humanReadableOutput = humanReadableOutput + "This Table is contains a headerColumn. Mapping may not be as accurate." + lineSep;
                    }
                    if(!table.getMappedColumns(headers2).isEmpty()){
                        System.out.println(table);
                        humanReadableOutput = humanReadableOutput + table + lineSep;
                        System.out.println(table.getMappedColumns(headers2));
                        humanReadableOutput = humanReadableOutput + table.getMappedColumns(headers2) + lineSep;
                        humanReadableOutput = humanReadableOutput + table.getHumanReadableMatches();
                    }
                    if(table.getSignificantUnmappedColumns() != (null)&& !table.getSignificantUnmappedColumns().isEmpty()){
                        System.out.println("signif Unmapped Cols: " + table.getSignificantUnmappedColumns());
                    }
                }
                writeHumanReadableOutput(humanReadableOutput, workspace);
                runNumber++;
                if(runNumber == maxRunNumber){
                    break;
                }
            }
        }
        else{
            String humanReadableOutput = "Current results of table mapping: ";
            System.out.println("Only the juicy stuff, we like low hanging fruit: ");
            System.out.println(relevanceVectorMap);
            String lineSep = System.getProperty("line.separator");
            humanReadableOutput = humanReadableOutput + relevanceVectorMap + lineSep;
            for(Table table : relevanceVectorMap.keySet()){
                if(table.isHeaderColumn()){
                    System.out.println("This Table is contains a headerColumn. Mapping may not be as accurate.");
                    humanReadableOutput = humanReadableOutput + "This Table is contains a headerColumn. Mapping may not be as accurate." + lineSep;
                }
                if(!table.getMappedColumns(headers2).isEmpty()){
                    System.out.println(table);
                    humanReadableOutput = humanReadableOutput + table + lineSep;
                    System.out.println(table.getMappedColumns(headers2));
                    humanReadableOutput = humanReadableOutput + table.getMappedColumns(headers2) + lineSep;
                    humanReadableOutput = humanReadableOutput + table.getHumanReadableMatches();
                }
                if(table.getSignificantUnmappedColumns() != (null)&& !table.getSignificantUnmappedColumns().isEmpty()){
                    System.out.println("signif Unmapped Cols: " + table.getSignificantUnmappedColumns());
                }
            }
            writeHumanReadableOutput(humanReadableOutput, workspace);
        }
    }

    /**
     * This method finds the XML files (using the findXMLs method) and read their properties. These are stored in the Table class.
     * @param workspace The workspace that was used during the run.
     */
    public static ArrayList<Table> readXMLFiles(String workspace,ArrayList<String> queries, boolean supportHeaderColumns, int requiredHeadersInHeaderColumn) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        System.out.println("Reading tables");
        ArrayList<String> XMLFiles = findXMLs(workspace);
        ArrayList<Table> tables = new ArrayList<Table>();
        for(String XMLFile : XMLFiles){
            Table currentTable = new Table(XMLFile, queries, supportHeaderColumns, requiredHeadersInHeaderColumn);
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

    /**
     * This method writes the collected debugContent to a debug file.
     * @param content A string containing the collected content
     * @param location The location for the method to write to.
     * @throws IOException When an incorrect path has been given.
     */
    private static void writeHumanReadableOutput(String content, String location) throws IOException {
        System.out.println("Writing to file: " + location + "/output.txt");
        FileWriter fileWriter;
        String writeLocation = location + "/output.txt";
        File newTextFile = new File(writeLocation);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(content);
        fileWriter.close();
    }
}
