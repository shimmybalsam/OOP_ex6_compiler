package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks a list of string where each string is a line of the code in sjavac.
 */
public class CheckList extends Checker {
    private static final String METHOD_CALL_REGEX = "^[a-zA-Z]+[\\w]*\\h*\\((.+?)*\\)\\h*(\\;|\\{)$";
    private static final String EMPTY_LINE_REGEX = "^\\s*$";
    private static final String FINAL_REGEX = "^(final)";
    private static final String OPEN_CURLY_BRACKETS = "{";
    private static final String IF_BLOCK = "if";
    private static final String WHILE_BLOCK = "while";
    private static final String END_LINE_SUFFIX = ";";
    private static final String COMMENT_PREFIX = "//";
    private static final String METHOD_STARTS_WITH_VOID = "void";
    private static final String VALID_FINAL_REGEX = "^final\\h+(int|boolean|String|double|char)\\h+" +
            "(([_][\\w]|[a-zA-Z])(\\w*)?)\\h*\\=\\h*[\\w\"']\\;$";
    private static final String VALID_VARIABLES_REGEX = "^([a-zA-Z]+[\\w]*|[_][\\w]+)\\h*\\=\\h*\\w\\;?$";
    private static final String SPLIT_BY_COMMA = ",";
    private static final String SPLIT_BY_SPACE = "\\h+";
    private static final String SPLIT_BY_EQUAL = "=";

    private List<String> lines;
    //List that containing arrays, where each array is a method in the code that need to be checked.
    private ArrayList<ArrayList<String>> multiSubList = new ArrayList<>();
    //Map that contains all the global variables.
    private Map<String, String> globalMap = new HashMap<>();
    //Map the contains all the final global variables.
    private Map<String, String> finalMap = new HashMap<>();
    //List that contains all the names of the methods in the code
    private List<String> methodsList = new ArrayList<>();
    //Array that contains all the variables maps.
    private ArrayList<Map<String, String>> allMapsList = new ArrayList<>();
    //Array that contains all the maps of the variables that were initialized.
    private ArrayList<Map<String, String>> initializeVerifier = new ArrayList<>();
    //Map that contains all the global variables that were initialized.
    private Map<String, String > globalInitializer = new HashMap<>();
    //Map contains the method names and the parameters that the method takes,as were declared in its signature.
    private Map<String, Map<String, String >> methodParamsMap = new HashMap<>();
    private Map<String, ArrayList<Map<String, String>>> methodCalls = new HashMap<>();

    /**
     * Constructor for the Check List class.
     * @param lines
     */
    public CheckList(List<String> lines){
        this.lines = lines;
    }

    /**
     * Checks the validation of all the lines in the code. The checking is in general terms, its not checks
     * the inner blocks, because these blocks are going to be checked separately and specifically in the
     * relevant classes
     * @throws Type1Exception if line is not valid.
     */
    @Override
    public void check() throws Type1Exception {
        Pattern methodCall = Pattern.compile(METHOD_CALL_REGEX);
        Pattern emptyLine = Pattern.compile(EMPTY_LINE_REGEX);
        boolean flag = false;
        ArrayList<String> subList;
        Pattern finalPattern = Pattern.compile(FINAL_REGEX);
        try{
            for (int i = 0; i < lines.size(); i++) {
                Matcher finalMatcher = finalPattern.matcher(lines.get(i));
                Matcher illegalMethodMatcher = methodCall.matcher(lines.get(i).trim());
                Matcher emptyLineMatcher = emptyLine.matcher(lines.get(i));
                if (isMethodStarter(i)) {
                    if (!lines.get(i).trim().endsWith(OPEN_CURLY_BRACKETS)) {
                        throw new Type1Exception();
                    }
                    subList = new ScopeGetter(i,true,lines,methodsList).getMethod();
                    i += subList.size() - 1;
                    multiSubList.add(subList);
                    flag = true;

                } else if (illegalMethodMatcher.find()){
                    throw new Type1Exception();

                }else if(lines.get(i).trim().startsWith(IF_BLOCK) ||
                        lines.get(i).trim().startsWith(WHILE_BLOCK)){
                    if (!lines.get(i).trim().endsWith(OPEN_CURLY_BRACKETS)){
                        throw new Type1Exception();
                    }
                    ifWhileOutOfMethod(i);

                }else if(finalMatcher.find()){
                    if (!lines.get(i).endsWith(END_LINE_SUFFIX)){
                        throw new Type1Exception();
                    }
                    updateFinalMap(i);
                    flag = true;

                }else if(lines.get(i).trim().startsWith(COMMENT_PREFIX)){
                    new CheckComment(lines.get(i)).check();
                    flag = true;

                }else if(!emptyLineMatcher.find()){
                    new CheckSuffix(lines.get(i)).check();
                }
            }
            allMapsList.add(finalMap);
            //adding an empty map to FINAL placing in initializeVerifier, to fit the fact that checking fo final
            // initializations is redundant; yet the placing is needed in order to keep the hierarchy of our maps.
            initializeVerifier.add(new HashMap<>());
            new GlobalMapBuilder(multiSubList, globalMap, allMapsList, initializeVerifier,
                    globalInitializer, lines).buildVariableMap();
            if (!globalMap.isEmpty()) {
                flag = true;
            }
            for (ArrayList<String> scopeMethod : multiSubList) {
                new CheckMethod(scopeMethod, allMapsList,initializeVerifier, methodParamsMap,
                        methodCalls).check();
                flag = true;
            }
            new CheckMethodCalls(methodCalls, methodsList, methodParamsMap).check();

        }catch (Type1Exception t){
            throw new Type1Exception();
        }
        if (!flag){
            throw new Type1Exception();
        }
    }

    /**
     * checks if line in the code is method, by checking if it starts with 'void'.
     * @param lineIndex the index of the line in the list.
     * @return true if its a start of a method.
     */
    private boolean isMethodStarter(int lineIndex){
        return lines.get(lineIndex).trim().startsWith(METHOD_STARTS_WITH_VOID);
    }

    /**
     * checks if there is a line with If/While signature inside of a method.
     * @param lineIndex the index of the line in the list
     */
    private void ifWhileOutOfMethod(int lineIndex){
        try{
            boolean flag = false;
            for(ArrayList<String> subScope: multiSubList){
                if(subScope.contains(lines.get(lineIndex))){
                    flag = true;
                }
            }if(!flag){
                throw new Type1Exception();
            }
        }catch (Type1Exception t){
            System.out.println(t.getMessage());
        }
    }

    /**
     * Updating the final variables map. for each line that matches the pattern of the regex that checks if
     * the line in the list starts with the word 'final', this method checks if this line has the valid
     * pattern for line that declared on a final variable. if it does, the method puts this variable in the
     * map that containing all the variables that were declared as final.
     * @param lineIndex the index of the line in the list.
     * @throws Type1Exception if its not a valid line.
     */
    private void updateFinalMap(int lineIndex) throws Type1Exception {
        String[] splitLine;
        String valueType;
        Pattern validFinalPattern = Pattern.compile(VALID_FINAL_REGEX);
        Matcher validFinalPatternMatcher = validFinalPattern.matcher(lines.get(lineIndex));
        Pattern variablesValid = Pattern.compile(VALID_VARIABLES_REGEX);
        if(validFinalPatternMatcher.find()) {
            splitLine = lines.get(lineIndex).split(SPLIT_BY_COMMA);
            valueType = splitLine[0].split(SPLIT_BY_SPACE)[1];
            String first = splitLine[0].trim().split(SPLIT_BY_EQUAL)[0].trim().split(SPLIT_BY_SPACE)[2].trim();
            if (!finalMap.containsKey(first) || finalMap.isEmpty()) {
                finalMap.put(first, valueType);
            }else{
                throw new Type1Exception();
            }
            if (splitLine.length == 1){
                if (!splitLine[0].trim().endsWith(END_LINE_SUFFIX)){
                    throw new Type1Exception();
                }
            }else {
                for (int i = 1; i < splitLine.length; i++) {
                    if (i != splitLine.length-1 && splitLine[i].trim().endsWith(END_LINE_SUFFIX)){
                        throw new Type1Exception();
                    }else if (splitLine.length-1 == i && !splitLine[i].trim().endsWith(END_LINE_SUFFIX)){
                        throw new Type1Exception();
                    }else {
                        Matcher variableMatcher = variablesValid.matcher(splitLine[i]);
                        if (variableMatcher.find()) {
                            String name = splitLine[i].trim().split(SPLIT_BY_EQUAL)[0].trim();
                            if (!finalMap.containsKey(name)) {
                                finalMap.put(name, valueType);
                            } else {
                                throw new Type1Exception();
                            }
                        } else {
                            throw new Type1Exception();
                        }
                    }
                }
            }
        }else{
            throw new Type1Exception();
        }
    }
}
