package oop.ex6.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class checks the validation of an comment line.
 */
public class CheckComment extends Checker {
    final static private String VALID_COMMENT_REGEX = "^(\\/\\/).+?$";

    String line;

    /**
     * Constructor for the CheckComment class.
     * @param line to be checked.
     */
    public CheckComment(String line) {
        this.line = line;
    }

    /**
     * Checking the line according to the Regular expression, that represents the valid comment line.
     * @throws Type1Exception if there wasn't a match between the line and the pattern of the Regex.
     */
    @Override
    public void check() throws Type1Exception {
        Pattern commentPattern = Pattern.compile(VALID_COMMENT_REGEX);
        Matcher commentMatcher = commentPattern.matcher(line);
        if(!commentMatcher.find()){
            throw new Type1Exception();
        }
    }
}
