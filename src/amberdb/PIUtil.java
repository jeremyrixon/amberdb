package amberdb;

/**
 * Borrowing of the Checksums/Damm Algorithm to provide check-digits for PI
 * from:
 * 
 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Checksums/Damm_Algorithm
 */
public class PIUtil {
    private static final String PI_PREFIX = "nla.obj-";
    static final char[][] taqDhmd111rr = 
           {{'0','3','1','7','5','9','8','6','4','2'},
            {'7','0','9','2','1','5','4','8','6','3'},
            {'4','2','0','6','8','7','1','3','5','9'},
            {'1','7','5','0','9','8','3','4','2','6'},
            {'6','1','2','3','0','4','5','9','7','8'},
            {'3','6','7','4','2','0','9','5','8','1'},
            {'5','8','6','9','7','2','0','1','3','4'},
            {'8','9','4','5','3','6','2','0','1','7'},
            {'9','4','3','8','6','1','7','2','0','5'},
            {'2','5','8','1','4','3','6','7','9','0'}};
    
    /**
     * taq: uses totally anti-symmetric quasigroup to generate the check digit
     *      for a number.
     * @param number
     * @return check digit.
     */
    public static int taq(Long number) {
        if (number == null)
            throw new IllegalArgumentException("The input objId is null.");
        
        char interim = '0';
        char[] numStr= new Long(number).toString().toCharArray();
        for (char digit : numStr) {
            interim = taqDhmd111rr[(interim - '0')][(digit - '0')];
        }
        return interim - '0';
    }
    
    public static String format(Long objId) {
        return PI_PREFIX + objId + taq(objId);
    }
    
    public static long parse(String pi) {
        if (!isValid(pi))
            throw new IllegalArgumentException("The input pi " + pi + " is invalid.");
        return new Long(pi.substring(pi.indexOf(PI_PREFIX) + 8, pi.length() - 1));
    }
    
    public static boolean isValid(String pi) {
        return (taq(new Long(pi.substring(pi.indexOf(PI_PREFIX) + 8))) == 0);
    }
}
