package oop.ex6.main;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that builds a local map per sub-scope.
 */
public class LocalMapBuilder {
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

    private String line;
    private Map<String, String> localMap;
    private Map<String, String > localInitializer;

    /**
     * Constructor for the Local Map Builder class.
     * @param line Line that contains variable.
     * @param localMap Map that contains the variables inside the method.
     * @param localInitializer Map contains the local variables that were initialized.
     */
    public LocalMapBuilder(String line, Map<String, String> localMap,Map<String, String > localInitializer ) {
        this.line = line;
        this.localMap = localMap;
        this.localInitializer = localInitializer;
    }

    /**
     * Checks the pattern of the variable with the relevant regex, if the variable is valid the method puts it
     * in the local variables map, where its name is the key and its type is the value. In addition to it, the
     * method updates the localInitializer map, if the variable initialized or not.
     * @throws Type1Exception if the variable is not valid.
     */
    public void buildLocalMap() throws Type1Exception {
        String valueType;
        String[] splitLine;
        Pattern variablesPattern = Pattern.compile(VARIABLES_REGEX);
        Matcher variablesMatcher = variablesPattern.matcher(line.trim());
        if (variablesMatcher.find()) {
            splitLine = line.split(SPLIT_BY_COMMA);
            valueType = splitLine[0].split(SPLIT_BY_SPACE)[0];
            String first = splitLine[0].trim().split(SPLIT_BY_EQUAL)[0].trim().split(SPLIT_BY_SPACE)[1].trim();
            if (splitLine[0].trim().split(SPLIT_BY_EQUAL).length == 1){
                localInitializer.put(first, NOT_INITIALIZED);
            }else{
                localInitializer.put(first, INITIALIZED);
            }
            if (first.endsWith(END_LINE_SUFFIX)){
                first = first.replace(END_LINE_SUFFIX,EMPTY_LINE);
            }
            if(!localMap.containsKey(first)){
                localMap.put(first, valueType);
            }else{
                throw new Type1Exception();
            }
            Pattern newPa = Pattern.compile(PARAMETERS_REGEX);
            for (int p = 1; p < splitLine.length; p++) {
                String[] splitByInitializer = splitLine[p].trim().split(SPLIT_BY_EQUAL);
                String addMe = splitByInitializer[0].trim();
                if (splitByInitializer.length == 1){
                    localInitializer.put(addMe, NOT_INITIALIZED);
                }else{
                    localInitializer.put(addMe, INITIALIZED);
                }
                if (addMe.endsWith(END_LINE_SUFFIX)){
                    addMe = addMe.replace(END_LINE_SUFFIX,EMPTY_LINE);
                }
                Matcher newPaMa = newPa.matcher(addMe);
                if (newPaMa.find()) {
                    if (!localMap.containsKey(addMe)) {
                        try {
                            new CheckReserved(addMe).check();
                            localMap.put(addMe, valueType);
                        } catch (Type1Exception t) {
                            throw new Type1Exception();
                        }

                    } else {
                        throw new Type1Exception();
                    }
                }else{
                    throw new Type1Exception();
                }
            }
        }
    }
}
