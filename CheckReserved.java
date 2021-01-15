package oop.ex6.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks that reserved sjavac words are not used as variables names.
 */
public class CheckReserved extends Checker {
    private static final String RESERVED_WORDS_REGEX = "^(int|String|boolean|double|char|void|final|if|while" +
            "|true|false|return)";
    private static final String VARIABLES_ASSIGNMENT_REGEX = "^(int|String|boolean|double|char)\\h+" +
            "([a-zA-Z]+[\\w]*|[_][\\w]+)[\\h]*(\\=\\h*(\\'\\W\\'|\\\"\\W*\\\"|\\-?\\h*[\\w\"']+" +
            "([\\.][\\d]+)?))?$";


    private String nameForCheck;

    /**
     * Constructor for the Check Reserved class.
     * @param nameForCheck The name of the variable for checking.
     */
    public CheckReserved(String nameForCheck) {
        this.nameForCheck = nameForCheck;
    }

    /**
     * Checks, by using regex, if the name of the variable is reserved word.
     * @throws Type1Exception If the name of the variable is not valid.
     */
    @Override
    public void check() throws Type1Exception {
        Pattern reservedPattern = Pattern.compile(RESERVED_WORDS_REGEX);
        Pattern variableSingleAssignment = Pattern.compile(VARIABLES_ASSIGNMENT_REGEX);
        Matcher singleMatcher = variableSingleAssignment.matcher(nameForCheck);
        Matcher reservedMatcher = reservedPattern.matcher(nameForCheck);
        if(!singleMatcher.find()) {
            if (reservedMatcher.find()) {
                throw new Type1Exception();
            }
        }
    }
}
