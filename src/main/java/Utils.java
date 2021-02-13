import static java.lang.Double.parseDouble;

public class Utils
{
    public static boolean isNumeric(String str) {
        try {
            parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
