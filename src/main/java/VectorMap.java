import java.util.*;

//Create general vector map
//Find similarity per vector

public class VectorMap {

    private Map<String, Integer> generalHeaderMap;
    private Map<String,Integer> generalTitleMap;

    public VectorMap(ArrayList<Table> tables){
        createGeneralHeaderVectorMap(tables);
        createGeneralTitleVectorMap(tables);

    }
    private void createGeneralHeaderVectorMap(ArrayList<Table> tables){
        Map<String, Integer> generalHeaderMap = new LinkedHashMap<String, Integer>();
        for(Table table : tables){
            ArrayList<String> headers = table.getHeaders();
            for(String header : headers){
                header = header.replace("ﬁ","fi");
                header = header.toLowerCase();
                if(generalHeaderMap.containsKey(header)){
                    generalHeaderMap.put(header, generalHeaderMap.get(header)+1);
                }
                else{
                    generalHeaderMap.put(header, 1);
                }
            }
        }
        generalHeaderMap = sortByValue(generalHeaderMap);
        this.generalHeaderMap = generalHeaderMap;
    }

    private void createGeneralTitleVectorMap(ArrayList<Table> tables) {
        Map<String, Integer> generalTitleMap = new LinkedHashMap<String, Integer>();
        for(Table table : tables){
            ArrayList<String> titles = table.getTitle();
            for(String title : titles){
                title = title.replace("ﬁ","fi");
                title = title.toLowerCase();
                if(generalTitleMap.containsKey(title)){
                    generalTitleMap.put(title, generalTitleMap.get(title)+1);
                }
                else{
                    generalTitleMap.put(title, 1);
                }
            }
        }
        generalTitleMap = sortByValue(generalTitleMap);
        this.generalTitleMap = generalTitleMap;

    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    public Map<String, Integer> getTitleMap(){
        return generalTitleMap;
    }
    public Map<String, Integer> getHeaderMap(){
        return generalHeaderMap;
    }

    public String toString(){
        String string = "";
        string = string + (generalHeaderMap) + "\n";
        string = string + ("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-") + "\n";
        for(String header : generalHeaderMap.keySet()){
            if(generalHeaderMap.get(header) >2){
                string = string + (header + " " + generalHeaderMap.get(header) + "\n");
            }
        }

        string = string + (generalTitleMap) + "\n";
        string = string + ("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-") + "\n";
        for(String title : generalTitleMap.keySet()){
            if(generalTitleMap.get(title) > 2){
                string = string + (title + " " + generalTitleMap.get(title)) + "\n";
            }
        }
        return string;
    }
}
