package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks inner method scopes of the code.
 */
public class CheckMethod extends Checker {
    private static final String VARIABLE_LINE_REGEX = "^(int|String|boolean|double|char)+\\h*[\\w]*";
    private static final String EMPTY_LINE_REGEX = "^\\s*$";
    private static final String RETURN_REGEX = "^(return)\\h*\\w*\\h*\\;$";
    private static final String VALID_RETURN_REGEX = "^(return)\\h*\\;$";
    private static final String CLOSING_SUFFIX_REGEX = "^\\s*\\}\\h*$";
    private static final String METHOD_CALL_REGEX = "^[a-zA-Z]+[\\w]*\\h*\\((.+?)*\\)\\h*\\;$";
    private static final String ASSIGNMNT_REGEX = "^([a-zA-Z]+[\\w]*|[_][\\w]+)\\h*\\=\\h*(\\'(\\W|\\w)\\'" +
            "|\\-?\\h*[\\w\"\\.]+([\\.+][\\d]+)?)\\;$";
    private static final String IF_BLOCK = "if";
    private static final String WHILE_BLOCK = "while";
    private static final String COMMENT_PREFIX = "//";
    private static final String METHOD_STARTS_WITH_VOID = "void";
    private static final String SPLIT_BY_EQUAL = "=";
    private static final String SPLIT_BY_COMMA = ",";
    private static final String SPLIT_BY_SPACE = "\\h+";
    private static final String CHECK_IF_WHILE = "If/While";
    private static final String CHECK_METHOD_CALL = "Method call";
    private static final String CHECK_VARIABLES = "Variables";
    private static final String METHOD_PARAMETERS_REGEX = "(\\()(.+?)(\\))";
    private static final String METHOD_NAME_REGEX = "(void)(.+?)(\\()";
    private static final String METHOD_VALID_NAME_REGEX = "^[a-zA-Z]+\\w*\\h*$";
    private static final String METHOD_SIGNATURE_REGEX = "^(void)\\h+[a-zA-Z]+[\\w]*[\\h]*\\([\\w,\\h]*\\)" +
            "\\h*\\{$";
    private static final String PARAMETERS_REGEX = "^(int|String|double|boolean|char)\\h+[a-zA-Z_]+[\\w]*$";
    private static final String FINAL_PARAMETERS_REGEX = "^final\\h+(int|String|double|boolean|char)\\" +
            "h+[a-zA-Z_]+[\\w]*$";
    private static final String INITIALIZED = "Initialized";



    private int depthCounter = 0;
    private String line;
    private List<String> subList;
    //Array that contains the lines of the inner scope in the method.
    private ArrayList<String> subScope;
    //Map that contains the variables inside the method.
    private Map<String, String> localMap = new HashMap<>();
    //Array of maps that contains all the variables that were verified and initialized.
    ArrayList<Map<String, String>> updatedAndVerifiedMaps;
    private ArrayList<Map<String, String>> allMapsList;
    //Map contains the local variables that were initialized.
    private Map<String, String> localInitializer = new HashMap<>();
    private ArrayList<Map<String, String>> initializeVerifier;
    private Map<String, Map<String, String >> methodParamsMap;
    private Map<String, ArrayList<Map<String, String>>> methodCalls;
    //Map that contains the parameters of the method that were declared as final.
    private Map<String, String> methodFinalParamsMap = new HashMap<>();

    /**
     * Constructor of the Check Method class.
     * @param subList the list that contains the lines of the method.
     * @param allMapsList Array that contains all the variables maps.
     * @param initializeVerifier Array that contains all the maps of the variables that were initialized so far.
     * @param methodParamsMap Map contains the method names and the parameters that the method takes,as were
     *                        declared in its signature.
     * @param methodCalls Map contains all the methods that there was call for them inside a method.
     */
    public CheckMethod(List<String> subList, ArrayList<Map<String, String>> allMapsList,
                       ArrayList<Map<String, String>> initializeVerifier, Map<String,
            Map<String, String>> methodParamsMap, Map<String, ArrayList<Map<String, String>>> methodCalls) {

        this.subList = subList;
        this.allMapsList = allMapsList;
        this.initializeVerifier = initializeVerifier;
        this.methodParamsMap = methodParamsMap;
        this.methodCalls = methodCalls;
    }

    /**
     * Checks the validation of all the lines in the method block.
     * @throws Type1Exception
     */
    @Override
    public void check() throws Type1Exception {
        String checkType;
        ArrayList<Integer> variableIndex = new ArrayList<>();
        boolean createdLocal = false;
        Pattern variableLinePattern = Pattern.compile(VARIABLE_LINE_REGEX);
        Pattern emptyLine = Pattern.compile(EMPTY_LINE_REGEX);
        Pattern returnPattern = Pattern.compile(RETURN_REGEX);
        Pattern returnValidPattern = Pattern.compile(VALID_RETURN_REGEX);
        Pattern closingSuffix = Pattern.compile(CLOSING_SUFFIX_REGEX);
        Pattern methodCall = Pattern.compile(METHOD_CALL_REGEX);
        Pattern assignmentPattern = Pattern.compile(ASSIGNMNT_REGEX);
        try{
            checkMethodSignature(subList.get(0));
        }catch (Type1Exception t){
            throw new Type1Exception();
        }
        for (int i = 1; i < subList.size(); i++){
            Matcher m1 = emptyLine.matcher(subList.get(i));
            Matcher m2 = returnPattern.matcher(subList.get(i).trim());
            Matcher m2a = returnValidPattern.matcher(subList.get(i).trim());
            Matcher m4 = variableLinePattern.matcher(subList.get(i));
            Matcher m5 = methodCall.matcher(subList.get(i).trim());
            Matcher m6 = assignmentPattern.matcher(subList.get(i).trim());
            try{
                if (subList.get(i).trim().startsWith(IF_BLOCK) || subList.get(i).trim().startsWith(WHILE_BLOCK)) {
                    subScope = new ScopeGetter(i,false, subList).getMethod();
                    checkType = CHECK_IF_WHILE;
                    new ScopesCheckMaker(localMap,localInitializer,allMapsList,initializeVerifier,depthCounter
                            ,subScope,methodCalls,updatedAndVerifiedMaps,createdLocal,checkType).checkMaker();
                    i += subScope.size() - 1;
                }
                else if(subList.get(i).trim().startsWith(METHOD_STARTS_WITH_VOID)){
                    throw new Type1Exception();
                }
                else if (m2.find()) {
                    Matcher m3 = closingSuffix.matcher(subList.get(i+1));
                    if (!m2a.find()){
                        throw new Type1Exception();
                    }
                    if (!m3.find()) {
                        throw new Type1Exception();
                    }
                }
                else if(m4.find()){
                    new CheckSuffix(subList.get(i)).check();
                    new LocalMapBuilder(subList.get(i), localMap, localInitializer).buildLocalMap();
                    createdLocal = true;
                    variableIndex.add(i);
                }
                else if (subList.get(i).trim().startsWith(COMMENT_PREFIX)){
                    new CheckComment(subList.get(i)).check();
                }
                else if(m5.find()) {
                    checkType = CHECK_METHOD_CALL;
                    line = subList.get(i);
                    new ScopesCheckMaker(localMap,localInitializer,allMapsList,initializeVerifier,
                            depthCounter,subScope,methodCalls,updatedAndVerifiedMaps,createdLocal,
                            checkType,line).checkMaker();
                }
                else if(m6.find()){
                    String name = subList.get(i).trim().split(SPLIT_BY_EQUAL)[0].trim();
                    if(methodFinalParamsMap.containsKey(name)){
                        throw new Type1Exception();
                    }
                    else{
                        line = subList.get(i);
                        checkType = CHECK_VARIABLES;
                        new ScopesCheckMaker(localMap,localInitializer,allMapsList,initializeVerifier,
                                depthCounter,subScope,methodCalls,updatedAndVerifiedMaps,createdLocal,
                                checkType,line).checkMaker();
                    }
                }
                else if (createdLocal || localMap.size() > 0){
                    Matcher m3 = closingSuffix.matcher(subList.get(i));
                    checkType = CHECK_VARIABLES;
                    if(variableIndex.size()==1){
                        line = subList.get(variableIndex.get(0));
                        new ScopesCheckMaker(localMap,localInitializer,allMapsList,initializeVerifier,
                                depthCounter,subScope,methodCalls,updatedAndVerifiedMaps,createdLocal,
                                checkType,line).checkMaker();
                    }
                    if (m3.find()) {
                        for (Integer aVariableIndex : variableIndex) {
                            line = subList.get(aVariableIndex);
                            new ScopesCheckMaker(localMap,localInitializer,allMapsList,initializeVerifier,
                                    depthCounter,subScope,methodCalls,updatedAndVerifiedMaps,createdLocal,
                                    checkType,line).checkMaker();
                        }
                    }
                }
                else {
                    Matcher m3 = closingSuffix.matcher(subList.get(i));
                    if (!m1.find() && !m3.find()) {
                        new CheckVariables(subList.get(i), allMapsList, initializeVerifier).check();
                    }
                }
            }catch (Type1Exception t){
                throw new Type1Exception();
            }
        }
    }

    private void checkMethodSignature(String line) throws Type1Exception {
        String[] split = null;
        Pattern methodValidName = Pattern.compile(METHOD_VALID_NAME_REGEX);
        Pattern methodSignature = Pattern.compile(METHOD_SIGNATURE_REGEX);
        Pattern methodName = Pattern.compile(METHOD_NAME_REGEX);
        Pattern inner = Pattern.compile(METHOD_PARAMETERS_REGEX);
        Matcher signatureMatcher = methodSignature.matcher(line.trim());
        Matcher innerMatcher = inner.matcher(line.trim());
        Matcher methodNameMatcher = methodName.matcher(line.trim());
        if(methodNameMatcher.find()){
            Matcher methodValidMatcher = methodValidName.matcher(methodNameMatcher.group(2).trim());
            if(methodValidMatcher.matches()) {
                try {
                    new CheckReserved(methodNameMatcher.group(2)).check();
                } catch (Type1Exception t) {
                    throw new Type1Exception();
                }
            }else{
                throw new Type1Exception();
            }
        }
        if (signatureMatcher.find()){
            while (innerMatcher.find()){
                split = innerMatcher.group(2).split(SPLIT_BY_COMMA);
            }
        }
        updateMethodParamsMap(split, methodNameMatcher);
    }

    /**
     * Updates the map that contains all the parameters of the method in its signature and their type.
     * @param splitMethodParams Array of strings containing the parameters from the method signature.
     * @param methodName The name of the method
     * @throws Type1Exception If the pattern of the parameters isn't valid, or there is more than one
     * parameter with the same name.
     */
    private void updateMethodParamsMap(String[] splitMethodParams, Matcher methodName) throws Type1Exception {
        Pattern params = Pattern.compile(PARAMETERS_REGEX);
        Pattern finalParams = Pattern.compile(FINAL_PARAMETERS_REGEX);
        Matcher paramMatcher;
        Matcher finalParamsMatcher;
        if (splitMethodParams != null) {
            Map<String, String> methodParams = new HashMap<>();
            for (String param : splitMethodParams) {
                paramMatcher = params.matcher(param.trim());
                finalParamsMatcher = finalParams.matcher(param.trim());
                if (!paramMatcher.find() && !finalParamsMatcher.find()) {
                    throw new Type1Exception();
                }
                else if(finalParamsMatcher.matches()){
                    String key = param.trim().split(SPLIT_BY_SPACE)[2].trim();
                    String value = param.trim().split(SPLIT_BY_SPACE)[1].trim();
                    if(!methodFinalParamsMap.containsKey(key)){
                        methodFinalParamsMap.put(key, value);
                    }
                    if (!methodParams.containsKey(key)) {
                        methodParams.put(key, value);
                    }else {
                        throw new Type1Exception();
                    }
                } else{
                    String key = param.trim().split(SPLIT_BY_SPACE)[1].trim();
                    String value = param.trim().split(SPLIT_BY_SPACE)[0].trim();
                    if (!methodParams.containsKey(key)) {
                        methodParams.put(key, value);
                    }else {
                        throw new Type1Exception();
                    }
                }
                paramMatcher.reset();
                finalParamsMatcher.reset();
            }
            methodName.reset();
            if (methodName.find()) {
                methodParamsMap.put(methodName.group(2).trim(), methodParams);
                for (String key: methodParams.keySet()){
                    localInitializer.put(key, INITIALIZED);
                    localMap.put(key, methodParams.get(key));
                }
            }else{
                throw new Type1Exception();
            }
        }else{
            methodParamsMap.put(methodName.group(2).trim(), new HashMap<>());
        }
    }
}
