//ESM (Extra String Methods)
//contains static methods for other classes.
public class ESM {
    public ESM() {
    }

    public static int countOccurrences(String string, String substring){
        int lastIndex = 0;
        int count = 0;
        while(lastIndex != -1){
            lastIndex = string.indexOf(substring,lastIndex);
            if( lastIndex != -1){
                count ++;
                lastIndex+=substring.length();
            }
        }
        return count;
    }
}
