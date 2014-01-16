import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to analyze the query for the retrieval of the similarity.
 */
public class Query {
    private Map<String,Integer> compareScoreTitle;
    private Map<String,Integer> compareScoreHeader;

    public Query(ArrayList<String> title, ArrayList headers, Map<String, Integer> vectorMapTitle, Map<String, Integer> vectorMapHeader){
        compare(title, headers, vectorMapTitle, vectorMapHeader);
    }

    private void compare(ArrayList<String> title, ArrayList<String> headers, Map<String, Integer> vectorMapTitle, Map<String, Integer>vectorMapHeader ){
        Map<String, Integer> compareScoreTitle = new HashMap<String, Integer>();
        Map<String,Integer> compareScoreHeader = new HashMap<String, Integer>();
        for(String word : title){
            int counter = 0;
            for(String cell : vectorMapTitle.keySet()){
                counter++;
//                if(word.equals(cell)&&compareScore.containsKey(word)){
//                    compareScore.put(word, compareScore.get(word)+1);
//                }
                if(word.equals(cell)){
                    compareScoreTitle.put(word, vectorMapTitle.get(cell));
                }
                if(vectorMapTitle.size() == counter&&!compareScoreTitle.containsKey(word)){
                    compareScoreTitle.put(word, 0);
                }
            }
        }
        this.compareScoreTitle = compareScoreTitle;
        for(String header : headers){
            int counter = 0;
            for(String cell: vectorMapHeader.keySet()){
                counter++;
                if(header.equals(cell)){
                    compareScoreHeader.put(header, vectorMapHeader.get(cell));
                }
                if(vectorMapHeader.size() == counter&&!compareScoreHeader.containsKey(header)){
                    compareScoreHeader.put(header, 0);
                }
            }
        }
        this.compareScoreHeader = compareScoreHeader;
    }

    /**
     * This method will rank each table in the list against the relevance scores according to the query.
     * @param tables The list of table objects from the corpus
     * @return A string containing the ID of the table and the score against the query.
     */
    public Map<Table, Integer> rankTableHeaders(ArrayList<Table> tables){
        Map<Table, Integer> tableRanking = new HashMap<Table, Integer>();

        for(Table table : tables){
            tableRanking.put(table, 0);
            //First we do the headers:
            ArrayList<String> headers = table.getHeaders();
            for(String queryHeader : headers){
                for(String header : compareScoreHeader.keySet()){
                    if(header.equals(queryHeader)){
                        tableRanking.put(table, tableRanking.get(table)+1);                                //OR: +compareScoreHeader.get(queryHeader)
                    }
                }
            }
        }
        return tableRanking;
    }

    public Map<Table, Integer> rankTableTitles(ArrayList<Table> tables){
        Map<Table, Integer> tableRanking = new HashMap<Table, Integer>();

        for(Table table : tables){
            tableRanking.put(table, 0);
            //First we do the headers:
            ArrayList<String> titles = table.getTitle();
            for(String queryTitle : titles){
                for(String title : compareScoreTitle.keySet()){
                    if(title.equals(queryTitle)){
                        tableRanking.put(table, tableRanking.get(table)+1);                                //OR: +compareScoreHeader.get(queryHeader)
                    }
                }
            }
        }
        return tableRanking;
    }

    public String toString(){
        String string = "Query against the title: \n";
        for(String word : compareScoreTitle.keySet()){
            string = string + word +" " + compareScoreTitle.get(word) +"\n";
        }
        string = string + "Query against the headers: \n";
        for(String header : compareScoreHeader.keySet()){
            string = string + header + " " + compareScoreHeader.get(header) +"\n";
        }
        return string;
    }
}
