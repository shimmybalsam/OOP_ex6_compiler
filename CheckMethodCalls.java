package oop.ex6.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks that method calls are equivalent to method's name and parameters.
 */
public class CheckMethodCalls extends Checker {
    private static final String STRING_REGEX = "^\\\"(.+?)\\\"$";
    private static final String INT_REGEX = "^\\-?\\d+$";
    private static final String DOUBLE_REGEX = "^\\-?\\d+(\\.\\d+)?$";
    private static final String BOOLEAN_REGEX = "^true|false|\\-?\\d+(\\.\\d+)?$";
    private static final String CHAR_REGEX = "^\\'.\\'$";
    private static final String OPEN_BRACKETS = "\\(";
    private static final String CLOSE_BRACKETS = "\\)";
    private static final String SPLIT_BY_COMMA = ",";
    private static final String STRING_TYPE = "String";
    private static final String INT_TYPE = "int";
    private static final String CHAR_TYPE = "char";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String DOUBLE_TYPE = "double";


    private Map<String, ArrayList<Map<String, String>>> methodCalls;
    private List<String> methodsList;
    private Map<String, Map<String, String >> methodParamsMap;

    /**
     * Constructor for the Check Method Calls class.
     * @param methodCalls Map of all the methods that there was a call for them.
     * @param methodsList List of all the names of the methods in the code.
     * @param methodParamsMap Map of all the methods and their parameters.
     */
    public CheckMethodCalls(Map<String, ArrayList<Map<String, String>>> methodCalls, List<String> methodsList,
                            Map<String, Map<String, String >> methodParamsMap) {
        this.methodCalls = methodCalls;
        this.methodsList = methodsList;
        this.methodParamsMap = methodParamsMap;
    }

    /**
     * Checks the validation of the calls that were made for methods.
     * @throws Type1Exception If the call is not valid.
     */
    @Override
    public void check() throws Type1Exception {
        Pattern stringPattern = Pattern.compile(STRING_REGEX);
        Pattern intPattern = Pattern.compile(INT_REGEX);
        Pattern charPattern = Pattern.compile(CHAR_REGEX);
        Pattern doublePattern = Pattern.compile(DOUBLE_REGEX);
        Pattern booleanPattern = Pattern.compile(BOOLEAN_REGEX);
        for(String methodCall: methodCalls.keySet()){
            String[] temp = methodCall.split(OPEN_BRACKETS);
            String methodName = temp[0].trim();
            String allParams = temp[1].split(CLOSE_BRACKETS)[0].trim();
            String[] separatedParams = allParams.split(SPLIT_BY_COMMA);
            if (methodsList.contains(methodName)){
                for (int i = 0; i< separatedParams.length; i++){
                    boolean flag = false;
                    String type;
                    Matcher stringMatcher = stringPattern.matcher(separatedParams[i].trim());
                    Matcher intMatcher = intPattern.matcher(separatedParams[i].trim());
                    Matcher charMatcher = charPattern.matcher(separatedParams[i].trim());
                    Matcher doubleMatcher = doublePattern.matcher(separatedParams[i].trim());
                    Matcher booleanMatcher = booleanPattern.matcher(separatedParams[i].trim());
                    if (stringMatcher.find()){
                        type = STRING_TYPE;
                        if(!paramsVerifier(type, methodName, i)){
                            throw new Type1Exception();
                        }
                    }else if(intMatcher.find()){
                        type = INT_TYPE;
                        if(!paramsVerifier(type, methodName, i)){
                            throw new Type1Exception();
                        }
                    }else if (charMatcher.find()){
                        type = CHAR_TYPE;
                        if(!paramsVerifier(type, methodName, i)){
                            throw new Type1Exception();
                        }
                    }else if(doubleMatcher.find()){
                        type = DOUBLE_TYPE;
                        if(!paramsVerifier(type, methodName, i)){
                            throw new Type1Exception();
                        }
                    }else if(booleanMatcher.find()){
                        type = BOOLEAN_TYPE;
                        if(!paramsVerifier(type, methodName, i)){
                            throw new Type1Exception();
                        }
                    }else{
                        for (Map<String, String> map: methodCalls.get(methodCall)){
                            if (map.containsKey(separatedParams[i].trim())){
                                type = map.get(separatedParams[i].trim());
                                if(paramsVerifier(type,methodName, i)){
                                    flag = true;
                                    break;
                                }
                            }else if(methodParamsMap.get(methodName).size() == 0){
                                flag = true;
                            }
                        }
                        if (!flag){
                            throw new Type1Exception();
                        }
                    }
                }
            }else{
                throw new Type1Exception();
            }
        }
    }

    /**
     * Verifying if the call was made with the right parameters.
     * @param type The type of the parameter that the call was made with.
     * @param methodName The name of the method in the call.
     * @param parameterLocation the position of the parameter in the call.
     * @return true if the call was valid.
     */
    private boolean paramsVerifier(String type, String methodName, int parameterLocation){
        if(methodParamsMap.containsKey(methodName)){
            int j = 0;
            for (String key: methodParamsMap.get(methodName).keySet()){
                if (j == parameterLocation){
                    if(!methodParamsMap.get(methodName).get(key).equals(type)){
                        return false;
                    }
                    j++;
                }else{
                    j++;
                }
            }
            return true;
        }
        return false;
    }
}
