package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks inner if/while blocks within the code.
 */
public class CheckIfWhile extends Checker {
    private static final String VARIABLE_LINE_REGEX = "^(int|String|boolean|double|char)+\\h*[\\w]*";
    private static final String EMPTY_LINE_REGEX = "^\\s*$";
    private static final String CLOSING_SUFFIX_REGEX = "^\\h*\\}\\h*$";
    private static final String METHOD_CALL_REGEX = "^[a-zA-Z]+[\\w]*\\h*\\((.+?)*\\)\\h*\\;$";
    private static final String IF_BLOCK = "if";
    private static final String WHILE_BLOCK = "while";
    private static final String CHECK_IF_WHILE = "If/While";
    private static final String COMMENT_PREFIX = "//";
    private static final String CHECK_METHOD_CALL = "Method call";
    private static final String CHECK_VARIABLES = "Variables";
    private static final String IF_WHILE_SIGNATURE_REGEX = "^(if|while)\\h*\\(\\h*[\\w]+[\\w\\.]*\\h*" +
            "(\\h*(\\|\\||\\&\\&)\\h*[\\w]+[\\w\\.]*\\h*)*?\\h*\\)\\h*\\{$";
    private static final String CONDITION_REGEX = "(\\()(.+?)(\\))";
    private static final String CONDITION_SPLIT_REGEX = "\\|\\||\\&\\&";
    private static final String LEGAL_CONSTANT_CONDITION_REGEX = "^true|false|\\-?\\d+(\\.\\d+)?$";
    private static final String INT_TYPE = "int";
    private static final String DOUBLE_TYPE = "double";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String INITIALIZED = "Initialized";


    private List<String> scope;
    private String line;
    private Map<String, String>  ifWhileMap = new HashMap<>();
    private ArrayList<String> subScope;
    private int depth;
    private ArrayList<Map<String, String>> fathersAllMapsList;
    private ArrayList<Map<String, String>> initializeVerifier;
    private Map<String, String> ifWhileInitializer = new HashMap<>();
    private Map<String, ArrayList<Map<String, String>>> methodCalls;
    ArrayList<Map<String, String>> updatedAndVerifiedMaps;

    /**
     * Constructor for the If/While class
     * @param scope the block of the the If/While to check
     * @param depth the depth of the block in the code.
     * @param allMapsList list of maps containing all the variables in the code so far.
     * @param initializeVerifier array of maps containing all the variables that were initialized so far.
     * @param methodCalls map containing all the calls for methods inside a scope, and array of the maps
     *                    containing all the variables that were declared and initialized so far.
     */
    public CheckIfWhile(List<String> scope, int depth, ArrayList<Map<String, String>> allMapsList,
                        ArrayList<Map<String, String>> initializeVerifier, Map<String,
                        ArrayList<Map<String, String>>> methodCalls) {
        this.scope = scope;
        this.depth = depth;
        this.fathersAllMapsList = allMapsList;
        this.initializeVerifier = initializeVerifier;
        this.methodCalls = methodCalls;
    }

    /**
     * Checks the validation of the If/while block.
     * @throws Type1Exception
     */
    @Override
    public void check() throws Type1Exception {
        boolean createdLocal = false;
        String checkType;
        try{
            checkIfWhileSignature(scope.get(0));
        }catch (Type1Exception t){
            throw new Type1Exception();
        }
        Pattern variableLinePattern = Pattern.compile(VARIABLE_LINE_REGEX);
        Pattern emptyLine = Pattern.compile(EMPTY_LINE_REGEX);
        Pattern closingSuffix = Pattern.compile(CLOSING_SUFFIX_REGEX);
        Pattern methodCall = Pattern.compile(METHOD_CALL_REGEX);
        for (int i = 1; i < scope.size(); i++){
            Matcher m1 = emptyLine.matcher(scope.get(i));
            Matcher m3 = variableLinePattern.matcher(scope.get(i));
            Matcher m4 = methodCall.matcher(scope.get(i).trim());
            try {
                if (scope.get(i).trim().startsWith(IF_BLOCK) || scope.get(i).trim().startsWith(WHILE_BLOCK)){
                    if(depth<Integer.MAX_VALUE) {
                        subScope = new ScopeGetter(i, false, scope).getMethod();
                        checkType = CHECK_IF_WHILE;
                        new ScopesCheckMaker(ifWhileMap,ifWhileInitializer, fathersAllMapsList,
                                initializeVerifier, depth, subScope, methodCalls, updatedAndVerifiedMaps,
                                createdLocal,checkType).checkMaker();
                        i += subScope.size() - 1;
                    }else{
                        throw new Type1Exception();
                    }
                }
                else if (scope.get(i).trim().startsWith(COMMENT_PREFIX)){
                    new CheckComment(scope.get(i)).check();
                }
                else if(m3.find()){
                    new LocalMapBuilder(scope.get(i), ifWhileMap, ifWhileInitializer).buildLocalMap();
                    createdLocal = true;
                }else if (i+1==scope.size()) {
                    Matcher m2 = closingSuffix.matcher(scope.get(i));
                    if (!m2.find()) {
                        throw new Type1Exception();
                    }
                }
                else if(m4.find()) {
                    line = scope.get(i);
                    checkType = CHECK_METHOD_CALL;
                    new ScopesCheckMaker(ifWhileMap,ifWhileInitializer, fathersAllMapsList,
                            initializeVerifier, depth, subScope, methodCalls, updatedAndVerifiedMaps,
                            createdLocal,checkType, line).checkMaker();
                }
                else if (createdLocal || ifWhileMap.size() > 0){
                    Matcher m2 = closingSuffix.matcher(scope.get(i));
                    checkType = CHECK_VARIABLES;
                    line = scope.get(i);
                    if (!m1.find() && ! m2.find()) {
                        new ScopesCheckMaker(ifWhileMap,ifWhileInitializer, fathersAllMapsList,
                                initializeVerifier, depth, subScope, methodCalls, updatedAndVerifiedMaps,
                                createdLocal,checkType, line).checkMaker();
                    }
                }
                else{
                    Matcher m2 = closingSuffix.matcher(scope.get(i));
                    if (!m1.find() && ! m2.find()) {
                        new CheckVariables(scope.get(i), fathersAllMapsList, initializeVerifier);
                    }
                }
            }catch (Type1Exception t){
                throw new Type1Exception();
            }
        }
    }

    /**
     * Checks if the condition of the If/While if valid.
     * @param condition the condition of the If/While.
     * @param variablesMapIndex the current variables map index.
     * @return true if the condition is valid.
     */
    private boolean checkLegalBool(String condition, int variablesMapIndex) {
        if (fathersAllMapsList.get(variablesMapIndex).get(condition).equals(INT_TYPE) ||
                fathersAllMapsList.get(variablesMapIndex).get(condition).equals(DOUBLE_TYPE) ||
                fathersAllMapsList.get(variablesMapIndex).get(condition).equals(BOOLEAN_TYPE)) {
            if (initializeVerifier.get(variablesMapIndex).containsKey(condition)) {
                if (initializeVerifier.get(variablesMapIndex).get(condition).equals(INITIALIZED)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks the signature of the If/While by using regex with the valid pattern.
     * @param line the line of the If/While signature in the block.
     * @throws Type1Exception if the signature is not valid.
     */
    private void checkIfWhileSignature(String line) throws Type1Exception {
        String[] allConditions = null;
        String condition;
        Pattern ifWhileSignature = Pattern.compile(IF_WHILE_SIGNATURE_REGEX);
        Matcher signatureMatcher = ifWhileSignature.matcher(line.trim());
        Pattern inner = Pattern.compile(CONDITION_REGEX);
        Matcher innerMatcher = inner.matcher(line.trim());
        if (signatureMatcher.find()) {
            while (innerMatcher.find()) {
                allConditions = innerMatcher.group(2).split(CONDITION_SPLIT_REGEX);
            }
            if (allConditions!= null) {
                Pattern legalConstantCondition = Pattern.compile(LEGAL_CONSTANT_CONDITION_REGEX);
                for (String cond : allConditions) {
                    condition = cond.trim();
                    Matcher legalMatcher = legalConstantCondition.matcher(condition);
                    if(!legalMatcher.find()) {
                        boolean tempFlag = false;
                        for (int i = fathersAllMapsList.size() - 1; i >= FINAL; i--) {
                            if (fathersAllMapsList.get(i).containsKey(condition)) {
                                if (checkLegalBool(condition, i)) {
                                    tempFlag = true;
                                    break;
                                }
                            }
                        }
                        if (!tempFlag){
                            throw new Type1Exception();
                        }
                    }
                    legalMatcher.reset();
                }
            }else{
                throw new Type1Exception();
            }
        }else{
            throw new Type1Exception();
        }
    }
}
