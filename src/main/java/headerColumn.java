import java.util.ArrayList;

//This class will identify and extract the headers from a header Column.

public class HeaderColumn {
    private boolean containsHeaderColumn;
    private ArrayList<String> queries;
    private ArrayList<Column> columns;
    private Column headerColumn;

    public HeaderColumn(ArrayList<Column> columns, ArrayList<String> queries, int requiredHeadersInHeaderColumn){
        this.columns = columns;
        this.queries = queries;
        this.containsHeaderColumn = containsHeaderColumn(requiredHeadersInHeaderColumn);
        if(containsHeaderColumn){
            System.out.println("This table contains a Header Column! It's data should be read differently!");
            //TODO: Fix the swapColumns or make sure the program adds it to its output!
            //why not create a new Table variable?
//            swapColumnsToLines();
        }
    }

    /**
     * If this method returns true then the table contains a header column. The column is then stored in the private variable.
     * @param requiredHeadersInHeaderColumn The amount of headers required for the method to mark a column as being a headerColumn.
     * @return a boolean that is true when the table contains a headerColumn.
     */
    private boolean containsHeaderColumn(int requiredHeadersInHeaderColumn){
        int hits =0;
        int highestHit = 0;
        Column headerColumn = null;
        boolean containsHeaderColumn = false;
        for(Column column: columns){
            for(String query : queries){
                if(column.containsHeader(query)||column.mightContainHeader(query)){
                    hits +=1;
                }
            }
            if(hits>requiredHeadersInHeaderColumn){
                containsHeaderColumn = true;
                if(hits > highestHit){
                    highestHit = hits;
                    headerColumn = column;
                }
            }
            hits = 0;
        }
        if(containsHeaderColumn){
            this.headerColumn = headerColumn;
        }
        return containsHeaderColumn;
    }

    private void swapColumnsToLines(){
        ArrayList<String> headers = headerColumn.getContent();
        ArrayList<Column> newColumns = new ArrayList<Column>();
        int count = 0;
        for(String header : headers){
            ArrayList<String> newColumnContent = new ArrayList<String>();
            newColumnContent.add(header);
            for(Column column : columns){
                if(!column.equals(headerColumn)){
                    newColumnContent.add(column.getContent().get(count));
                }
            }
            Column column = new Column(newColumnContent);
            newColumns.add(column);
            count+=1;
        }
        count = 0;
        System.out.println(newColumns);
        this.columns = newColumns;
    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //Getters:

    public ArrayList<Column> getColumns() {
        return columns;
    }
    public boolean containsHeaderColumn() {
        return containsHeaderColumn;
    }
}
