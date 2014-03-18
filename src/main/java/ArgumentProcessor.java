import org.apache.commons.cli.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ArgumentProcessor {

    private String workspace;
    private ArrayList<String> headers;
    private ArrayList<String> title;
    private boolean supportHeaderColumns;
    private boolean supportIteration;

    /**
     * This method will process the arguments given by the user.
     * @param args the arguments given by the user
     * @throws ParseException
     */
    public ArgumentProcessor(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        Option help = new Option("H", "Help",false ,"This is the help file of TEA.");
        Option query = new Option("Q", "Query", true, "The full path to the query table.");
        Option workspace = new Option("W", "Workspace", true, "The full path to the workspace");
        Option headers = new Option("HE", "Headers", true, "A string containing the headers for the query. Also requires the title option.");
        Option title = new Option("T","Title", true, "A string containing the title for the query. Also requires the header option.");
        Option supportHeaderColumn = new Option("HC" , "HeaderColumn", false, "Give the algorithm header Column support.");

        options.addOption(help);
        options.addOption(query);
        options.addOption(workspace);
        options.addOption(headers);
        options.addOption(title);
        options.addOption(supportHeaderColumn);

        CommandLine line = null;
        try{
            line = parser.parse(options, args);
        }
        catch(UnrecognizedOptionException e){
            System.out.println("There was an option given by the user that isn't supported by TEALink. System shutting down.");
            System.exit(1);
        }
        catch(MissingArgumentException e){
            System.out.println("One of the options given by the user did not contain any value. System shutting down.");
            System.exit(1);
        }
        this.headers = setHeaders(line);
        this.title = setTitle(line);
        this.supportHeaderColumns = setHeaderColumnSupport(line);
        this.supportIteration = setSupportIteration(line);
        this.workspace = setWorkspace(line);
    }

    /**
     * Sets the workspace from the comand line.
     * @param line the commandline as extracted from the constructor.
     * @return a string containing the workspace.
     */
    private String setWorkspace(CommandLine line){
        String workspace = null;
        if(line.hasOption("W")){
            workspace = line.getOptionValue("W");
        }
        return workspace;
    }

    private ArrayList<String> setHeaders(CommandLine line){
        ArrayList<String> headers = new ArrayList<String>();
        if(line.hasOption("HE")){
            headers.addAll(Arrays.asList(line.getOptionValue("HE").split(",")));
        }
        return headers;
    }

    private ArrayList<String> setTitle(CommandLine line) {
        ArrayList<String> title = new ArrayList<String>();
        if(line.hasOption("T")){
            title.addAll(Arrays.asList(line.getOptionValue("T").split(",")));
        }
        return title;
    }

    private boolean setHeaderColumnSupport(CommandLine line){
        boolean supportsHeaderColumns = false;
        if(line.hasOption("HC")){
            supportsHeaderColumns = true;
        }
        return supportsHeaderColumns;
    }
    private boolean setSupportIteration(CommandLine line){
        boolean supportsIteration = false;
        if(line.hasOption("I")){
            supportsIteration = true;
        }
        return supportsIteration;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public boolean supportHeaderColumns() {
        return supportHeaderColumns;
    }

    public boolean supportIteration(){
        return supportIteration;
    }
    public String getWorkspace(){
        return workspace;
    }
}
