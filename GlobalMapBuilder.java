package oop.ex6.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that builds the map of the global variables.
 */
public class GlobalMapBuilder {
    private static final String VARIABLES_REGEX = "^(int|String|boolean|double|char)\\h+([a-zA-Z]+[\\w]*|" +
            "[_][\\w]+)[\\h]*";
    private static final String PARAMETERS_REGEX = "^([a-zA-Z]+[\\w]*|[_][\\w]+)\\h*";
    private static final String SPLIT_BY_COMMA = ",";
    private static final String SPLIT_BY_SPACE = "\\h+";
    private static final String SPLIT_BY_EQUAL = "=";
    private static final String END_LINE_SUFFIX = ";";
    private static final String EMPTY_LINE = "";
    private static final String INITIALIZED = "Initialized";
    private static final String NOT_INITIALIZED = "Not initialized";

    private ArrayList<ArrayList<String>> multiSubList;
    private Map<String, String> globalMap;
    private ArrayList<Map<String, String>> allMapsList;
    private ArrayList<Map<String, String>> initializeVerifier;
    private Map<String, String > globalInitializer;
    private List<String> lines;
    private List<String> linesCopy = new ArrayList<>();

    /**
     * Constructor of the Global Map Builder class.
     * @param multiSubList List that containing arrays, where each array is a method in the code that need
     *                     to be checked.
     * @param globalMap Map that contains all the global variables.
     * @param allMapsList Array that contains all the variables maps.
     * @param initializeVerifier Array that contains all the maps of the variables that were initialized.
     * @param globalInitializer Map that contains all the global variables that were initialized.
     * @param lines The list that contains all the lines of the sjava file.
     */
    public GlobalMapBuilder(ArrayList<ArrayList<String>> multiSubList, Map<String, String> globalMap,
                            ArrayList<Map<String, String>> allMapsList,
                            ArrayList<Map<String, String>> initializeVerifier,
                            Map<String, String> globalInitializer, List<String> lines) {

        this.multiSubList = multiSubList;
        this.globalMap = globalMap;
        this.allMapsList = allMapsList;
        this.initializeVerifier = initializeVerifier;
        this.globalInitializer = globalInitializer;
        this.lines = lines;
    }

    /**
     * Updates the global variables map. The method first of all, coping all the original lines to a new list,
     * that checks and marks all the lines that are inside of a method. If the line matches the pattern of
     * declaration of variable, this variable is add to the global variables map and the initializeVerifier
     * updates if this variable was initialized or nor, else it checks if the variable is valid by using the
     * CheckVariables class.
     * @throws Type1Exception if the line is not in the form of valid line.
     */
    public void buildVariableMap() throws Type1Exception {
        allMapsList.add(globalMap);
        initializeVerifier.add(globalInitializer);
        String valueType;
        String[] splitLine;
        copyAndMark();
        Pattern variablesPattern = Pattern.compile(VARIABLES_REGEX);
        for (String aLinesCopy : linesCopy) {
            Matcher variablesMatcher = variablesPattern.matcher(aLinesCopy.trim());
            if (variablesMatcher.find()) {
                splitLine = aLinesCopy.split(SPLIT_BY_COMMA);
                valueType = splitLine[0].trim().split(SPLIT_BY_SPACE)[0];
                String first = splitLine[0].trim().split(SPLIT_BY_EQUAL)[0].trim().
                        split(SPLIT_BY_SPACE)[1].trim();
                if (first.endsWith(END_LINE_SUFFIX)) {
                    first = first.replace(END_LINE_SUFFIX, EMPTY_LINE);
                }
                if (splitLine[0].trim().split(SPLIT_BY_EQUAL).length == 1) {
                    globalInitializer.put(first, NOT_INITIALIZED);
                } else {
                    globalInitializer.put(first, INITIALIZED);
                }
                if (!globalMap.containsKey(first)) {
                    globalMap.put(first, valueType);
                } else {
                    throw new Type1Exception();
                }
                for (int p = 1; p < splitLine.length; p++) {
                    String[] splitByInitializer = splitLine[p].trim().split(SPLIT_BY_EQUAL);
                    String addMe = splitByInitializer[0].trim();
                    if (splitByInitializer.length == 1) {
                        globalInitializer.put(addMe, NOT_INITIALIZED);
                    } else {
                        globalInitializer.put(addMe, INITIALIZED);
                    }
                    if (addMe.endsWith(END_LINE_SUFFIX)) {
                        addMe = addMe.replace(END_LINE_SUFFIX, EMPTY_LINE);
                    }
                    Pattern newParameter = Pattern.compile(PARAMETERS_REGEX);
                    Matcher newParameterMatcher = newParameter.matcher(addMe);
                    if (newParameterMatcher.find()) {
                        if (!globalMap.containsKey(addMe)) {
                            try {
                                new CheckReserved(addMe).check();
                                globalMap.put(addMe, valueType);
                            } catch (Type1Exception t) {
                                throw new Type1Exception();
                            }

                        } else {
                            throw new Type1Exception();
                        }
                    } else {
                        throw new Type1Exception();
                    }
                }
                try {
                    new CheckVariables(aLinesCopy, allMapsList, initializeVerifier).check();
                } catch (Type1Exception t) {
                    throw new Type1Exception();
                }
            }

        }
    }

    /**
     * Copies and mark all the lines that inside a method, so these lines won't checked.
     */
    private void copyAndMark(){
        for (String line: lines) {
            linesCopy.add(line);
        }
        for (int i = 0; i < linesCopy.size(); i++) {
            for (ArrayList<String> sub : multiSubList) {
                if (sub.contains(linesCopy.get(i)))
                    linesCopy.set(i, "");
            }
        }
    }
}
