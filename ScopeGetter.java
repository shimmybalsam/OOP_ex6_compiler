package oop.ex6.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that builds an array of the inner scope, method block or If/While block.
 */
public class ScopeGetter {
    final static private String OPEN_CURLY_BRACKETS = "{";
    final static private String CLOSE_CURLY_BRACKETS = "}";
    final static private int COUNTER_ZERO = 0;
    final static private String METHOD_NAME_REGEX = "(void)(.+?)(\\()";

    private int scopeStartIndex;
    private boolean isMethod;
    private List<String> lines;
    private List<String> methodsList;

    /**
     * First constructor of the Scope Getter class, relevant for method blocks.
     * @param scopeStatIndex The index of the line the contains the signature of the block.
     * @param isMethod true if its a block of method.
     * @param lines the list of lines that contains the block.
     * @param methodsList List contains the names of all the methods if the file.
     */
    public ScopeGetter(int scopeStatIndex, boolean isMethod, List<String> lines, List<String> methodsList) {
        this.scopeStartIndex = scopeStatIndex;
        this.isMethod = isMethod;
        this.lines = lines;
        this.methodsList = methodsList;
    }

    /**
     * Second constructor of the Scope Getter class, relevant for If/While blocks.
     * @param scopeStartIndex The index of the line the contains the signature of the block.
     * @param isMethod false if its a block of method.
     * @param lines the list of lines that contains the block.
     */
    public ScopeGetter(int scopeStartIndex, boolean isMethod, List<String> lines) {
        this.scopeStartIndex = scopeStartIndex;
        this.isMethod = isMethod;
        this.lines = lines;
    }

    /**
     * Builds array that contains the relevant lines for the block.
     * @return array contains the lines of the method or the If/While block.
     * @throws Type1Exception If the form of the block is not valid.
     */
    public ArrayList<String> getMethod() throws Type1Exception {
        int counter = 1;
        ArrayList<String> subList = new ArrayList<>();
        subList.add(lines.get(scopeStartIndex));
        scopeStartIndex++;
        while (scopeStartIndex < lines.size() && counter != COUNTER_ZERO) {
            if (lines.get(scopeStartIndex).trim().endsWith(OPEN_CURLY_BRACKETS)) {
                counter++;
                subList.add(lines.get(scopeStartIndex));
                scopeStartIndex++;
            } else if (lines.get(scopeStartIndex).trim().endsWith(CLOSE_CURLY_BRACKETS)) {
                counter--;
                subList.add(lines.get(scopeStartIndex));
                scopeStartIndex++;
            } else {
                subList.add(lines.get(scopeStartIndex));
                scopeStartIndex++;
            }
        }
        if (counter == COUNTER_ZERO){
            if(isMethod){
                methodListUpdate(subList);
            }
            return subList;
        }else{
            throw new Type1Exception();
        }
    }

    /**
     * If the block is a method block, this method checks if its name is not already in the list that contains
     * the names of all the methods in the file.
     * @param subList The array that contains the lines of the block.
     * @throws Type1Exception if the name is already exists.
     */
    private void methodListUpdate(ArrayList<String> subList) throws Type1Exception {
        Pattern methodNamePattern = Pattern.compile(METHOD_NAME_REGEX);
        Matcher methodMatcher = methodNamePattern.matcher(subList.get(0));
        if (methodMatcher.find()) {
            if (!methodsList.contains(methodMatcher.group(2).trim())) {
                methodsList.add(methodMatcher.group(2).trim());
            }else{
                throw new Type1Exception();
            }
        }
    }
}
