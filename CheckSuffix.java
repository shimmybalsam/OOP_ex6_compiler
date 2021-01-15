package oop.ex6.main;

/**
 * Class that checks that line ends with ";" (unless its a comment).
 */
public class CheckSuffix extends Checker {
    private static final String END_LINE_SUFFIX = ";";
    private static final String INVALID_COMMENT_PREFIX = "/*";

    private String line;

    /**
     * Constructor for the Check Suffix class.
     * @param line The line that need to be checked.
     */
    public CheckSuffix(String line) {
        this.line = line.trim();
    }

    /**
     * Checks the suffix of the line.
     * @throws Type1Exception If the suffix is not valid.
     */
    @Override
    public void check() throws Type1Exception {
        if(!line.endsWith(END_LINE_SUFFIX)){
            throw new Type1Exception();
        }else if (line.startsWith(INVALID_COMMENT_PREFIX)){
            throw new Type1Exception();
        }
    }
}
