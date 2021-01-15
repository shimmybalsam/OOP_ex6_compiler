package oop.ex6.main;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that checks variable declaration/assignment lines.
 */
public class CheckVariables extends Checker {
    final static private String FINAL_SINGLE_REGEX = "^final\\h+(int|boolean|String|double|char)\\h+" +
            "([_][\\w]+|[a-zA-Z]\\w*)\\h*\\=\\h*\\-?\\h*[\\W|\\w\"']+([\\.+][\\d]+)?\\;$";
    final static private String FINAL_MULTI_REGEX = "^final\\h+(int|boolean|String|double|char)\\h+" +
            "([_][\\w]+|[a-zA-Z]\\w*)\\h*\\=\\h*\\-?\\h*[\\W|\\w\"']+([\\.+][\\d]+)?\\,\\h*(.+?)+\\;$";
    final static private String SINGLE_VARIABLE_REGEX = "^(int|String|boolean|double|char)\\h+" +
            "([a-zA-Z]+[\\w]*|[_][\\w]+)[\\h]*\\=\\h*(\\'\\W\\'|\\\"\\W*\\\"|\\-?\\h*[\\w\"']+([\\.][\\d]+)?)\\h*\\;$";
    final static private String MULTI_VARIABLE_REGEX = "^(int|String|boolean|double|char)\\h+" +
            "([a-zA-Z]+[\\w]*|[_][\\w]+)[\\h]*(\\=\\h*\\-?\\h*[\\W|\\w\"']+([\\.+][\\d]+)?)?\\,(.+?)+\\;$";
    final static private String VARIABLE_ASSIGNMENT_REGEX = "^([a-zA-Z]+[\\w]*|[_][\\w]+)\\h*\\=\\h*\\-?" +
            "\\h*[\\W|\\w\"']+([\\.+][\\d]+)?\\;$";
    final static private String VARIABLE_DECLARATION_REGEX = "^(int|String|boolean|double|char)\\h+" +
            "([a-zA-Z]+[\\w]*|[_][\\w]+)[\\h]*\\;$";
    final static private String INT_REGEX = "^\\-?\\d+$";
    final static private String STRING_REGEX = "^\\\"(.+?)\\\"$";
    final static private String CHAR_REGEX = "^\\'.\\'$";
    final static private String DOUBLE_REGEX = "^\\-?\\d+(\\.\\d+)?$";
    final static private String BOOLEAN_REGEX = "^true|false|\\-?\\d+(\\.\\d+)?$";
    private static final String STRING_TYPE = "String";
    private static final String INT_TYPE = "int";
    private static final String CHAR_TYPE = "char";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String DOUBLE_TYPE = "double";
    private static final String SPLIT_BY_COMMA = ",";
    private static final String SPLIT_BY_EQUAL = "=";
    private static final String SPLIT_BY_SPACE = "\\h+";
    private static final String INITIALIZED = "Initialized";
    private static final String END_LINE_SUFFIX = ";";
    private static final String EMPTY_STRING = "";

    private String line;
    private ArrayList<Map<String, String>> mapList;
    private ArrayList<Map<String, String>> initializeVerifier;

    public CheckVariables(String line, ArrayList<Map<String, String>> mapList,
                          ArrayList<Map<String, String>> initializeVerifier) {
        this.line = line.trim();
        this.mapList = mapList;
        this.initializeVerifier = initializeVerifier;
    }

    /**
     * checks if the variables are valid in any terms. If its needed the method checks the maps of the
     * variable in the scopes that appeared before the variable that is checked.
     * @throws Type1Exception
     */
    @Override
    public void check() throws Type1Exception {
        String[] split = null;
        String type;
        boolean flag;
        Matcher valueMatcher;
        Pattern finalSinglePattern = Pattern.compile(FINAL_SINGLE_REGEX);
        Pattern finalMultiPattern = Pattern.compile(FINAL_MULTI_REGEX);
        Pattern variableSinglePattern = Pattern.compile(SINGLE_VARIABLE_REGEX);
        Pattern variableMultiPattern = Pattern.compile(MULTI_VARIABLE_REGEX);
        Pattern variableAssignmentPattern = Pattern.compile(VARIABLE_ASSIGNMENT_REGEX);
        Pattern variableDeclarationPattern = Pattern.compile(VARIABLE_DECLARATION_REGEX);
        Matcher finalSingleMatcher = finalSinglePattern.matcher(line);
        Matcher finalMultiMatcher = finalMultiPattern.matcher(line);
        Matcher variableSingleMatcher = variableSinglePattern.matcher(line);
        Matcher variableMultiMatcher = variableMultiPattern.matcher(line);
        Matcher variableAssignmentMatcher = variableAssignmentPattern.matcher(line);
        Matcher variableDeclarationMatcher = variableDeclarationPattern.matcher(line);
        Pattern stringPattern = Pattern.compile(STRING_REGEX);
        Pattern intPattern = Pattern.compile(INT_REGEX);
        Pattern charPattern = Pattern.compile(CHAR_REGEX);
        Pattern doublePattern = Pattern.compile(DOUBLE_REGEX);
        Pattern booleanPattern = Pattern.compile(BOOLEAN_REGEX);
        if (finalSingleMatcher.find()){
            split = line.trim().split(SPLIT_BY_EQUAL);
            String name = split[0].trim().split(SPLIT_BY_SPACE)[2].trim();
            String value = split[1].replace(END_LINE_SUFFIX, EMPTY_STRING).trim();
            if (mapList.get(FINAL).containsKey(name)){
                type = mapList.get(FINAL).get(name);
                switch (type){
                    case STRING_TYPE:
                        valueMatcher = stringPattern.matcher(value);
                        if (!valueMatcher.find()){
                            if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                    equals(type)) {
                                throw new Type1Exception();
                            }
                        }
                        break;
                    case INT_TYPE:
                        valueMatcher = intPattern.matcher(value);
                        if(!valueMatcher.find()){
                            if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                    equals(type)) {
                                throw new Type1Exception();
                            }
                        }
                        break;
                    case DOUBLE_TYPE:
                        valueMatcher = doublePattern.matcher(value);
                        if(!valueMatcher.find()){
                            if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                    equals(type)) {
                                throw new Type1Exception();
                            }                        }
                        break;
                    case CHAR_TYPE:
                        valueMatcher = charPattern.matcher(value);
                        if(!valueMatcher.find()){
                            if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                    equals(type)) {
                                throw new Type1Exception();
                            }                        }
                        break;
                    case BOOLEAN_TYPE:
                        valueMatcher = booleanPattern.matcher(value);
                        if(!valueMatcher.find()){
                            if (!mapList.get(FINAL).containsKey(value) || !(mapList.get(FINAL).get(value).
                                    equals(type) ||
                                    mapList.get(FINAL).get(value).equals("int") ||
                                    mapList.get(FINAL).get(value).equals("double"))){
                                throw new Type1Exception();
                            }                       }
                        break;
                    default:
                        throw new Type1Exception();
                }
            }else{
                throw new Type1Exception();
            }
        }else if(finalMultiMatcher.find()){
            split = line.trim().split(SPLIT_BY_COMMA);
            String[] firstPart = split[0].trim().split(SPLIT_BY_EQUAL);
            String tempName = firstPart[0].trim().split(SPLIT_BY_SPACE)[2];
            String tempValue = firstPart[1].trim();
            split[0] = tempName+"="+tempValue;
            for (String part: split){
                String[] anotherSplit = part.trim().split(SPLIT_BY_EQUAL);
                String name = anotherSplit[0].trim();
                String value = anotherSplit[1].trim();
                if (value.endsWith(END_LINE_SUFFIX)){
                    value = value.replace(END_LINE_SUFFIX, EMPTY_STRING).trim();
                }
                if (mapList.get(FINAL).containsKey(name)){
                    type = mapList.get(FINAL).get(name);
                    switch (type){
                        case STRING_TYPE:
                            valueMatcher = stringPattern.matcher(value);
                            if (!valueMatcher.find()){
                                if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                        equals(type)) {
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case INT_TYPE:
                            valueMatcher = intPattern.matcher(value);
                            if(!valueMatcher.find()){
                                if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).equals(type)) {
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case DOUBLE_TYPE:
                            valueMatcher = doublePattern.matcher(value);
                            if(!valueMatcher.find()){
                                if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                        equals(type)) {
                                    throw new Type1Exception();
                                }                        }
                            break;
                        case CHAR_TYPE:
                            valueMatcher = charPattern.matcher(value);
                            if(!valueMatcher.find()){
                                if (!mapList.get(FINAL).containsKey(value) || !mapList.get(FINAL).get(value).
                                        equals(type)) {
                                    throw new Type1Exception();
                                }                        }
                            break;
                        case BOOLEAN_TYPE:
                            valueMatcher = booleanPattern.matcher(value);
                            if(!valueMatcher.find()){
                                if (!mapList.get(FINAL).containsKey(value) || !(mapList.get(FINAL).get(value).
                                        equals(type) ||
                                        mapList.get(FINAL).get(value).equals("int") ||
                                        mapList.get(FINAL).get(value).equals("double"))){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        default:
                            throw new Type1Exception();
                    }
                }else{
                    throw new Type1Exception();
                }
            }

        }else if (variableSingleMatcher.find()){
            split = line.trim().split(SPLIT_BY_EQUAL);
            String name = split[0].trim().split(SPLIT_BY_SPACE)[1].trim();
            String value = split[1].replace(END_LINE_SUFFIX, EMPTY_STRING).trim();
            flag = false;
            boolean flag2 = false;
            for(int i = mapList.size()-1; i>=FINAL; i--){
                if(mapList.get(i) == null){
                    i--;
                }
                if (mapList.get(i).containsKey(name)){
                    flag = true;
                    type = mapList.get(i).get(name);
                    switch (type){
                        case STRING_TYPE:
                            valueMatcher = stringPattern.matcher(value);
                            if (!valueMatcher.find()){
                                for (int j = mapList.size()-1; j>=FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value).
                                            equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case INT_TYPE:
                            valueMatcher = intPattern.matcher(value);
                            if(!valueMatcher.find()){
                                for (int j = mapList.size()-1; j>=FINAL; j--) {
                                    if(mapList.get(j)==null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value).
                                            equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }
                                        else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case DOUBLE_TYPE:
                            valueMatcher = doublePattern.matcher(value);
                            if(!valueMatcher.find()){
                                for (int j = mapList.size()-1; j>=FINAL; j--) {
                                    if(mapList.get(j) ==null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value).
                                            equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case CHAR_TYPE:
                            valueMatcher = charPattern.matcher(value);
                            if(!valueMatcher.find()){
                                for (int j = mapList.size()-1; j>=FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value).
                                            equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case BOOLEAN_TYPE:
                            valueMatcher = booleanPattern.matcher(value);
                            if(!valueMatcher.find()){
                                for (int j = mapList.size()-1; j>=FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if (mapList.get(j).containsKey(value) && (mapList.get(j).get(value).
                                            equals(type) ||
                                            mapList.get(j).get(value).equals(INT_TYPE) ||
                                            mapList.get(j).get(value).equals(DOUBLE_TYPE))){
                                        flag2 = true;
                                    }
                                }
                                if(!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        default:
                            throw new Type1Exception();
                    }
                    break;
                }
            }
            if (!flag){
                throw new Type1Exception();
            }
        }else if(variableMultiMatcher.find()){
            split = line.trim().split(SPLIT_BY_COMMA);
            String[] firstPart = split[0].trim().split(SPLIT_BY_EQUAL);
            String tempName = firstPart[0].trim().split(SPLIT_BY_SPACE)[1];
            if (firstPart.length > 1) {
                String tempValue = firstPart[1].trim();
                split[0] = tempName+"="+tempValue;
            }else{
                split[0] = tempName;
            }
            for (String part: split){
                Pattern illegalPattern = Pattern.compile("^(int|String|boolean|double|char)\\h*");
                Matcher illegalMatcher = illegalPattern.matcher(part.trim());
                if (illegalMatcher.find()){
                    throw new Type1Exception();
                }
                String[] anotherSplit = part.trim().split(SPLIT_BY_EQUAL);
                String name = anotherSplit[0].trim();
                if (anotherSplit.length > 1){
                    String value = anotherSplit[1].trim();
                    if(value.endsWith(END_LINE_SUFFIX)){
                        value = value.replace(END_LINE_SUFFIX, EMPTY_STRING).trim();
                    }
                    flag = false;
                    boolean flag2 = false;
                    for(int i = mapList.size()-1; i>=FINAL; i--) {
                        if (mapList.get(i).containsKey(name)) {
                            flag = true;
                            type = mapList.get(i).get(name);
                            switch (type) {
                                case STRING_TYPE:
                                    valueMatcher = stringPattern.matcher(value);
                                    if (!valueMatcher.find()) {
                                        for (int j = mapList.size()-1; j >= FINAL; j--) {
                                            if(mapList.get(j) == null){
                                                j--;
                                            }
                                            if(mapList.get(j).containsKey(value) && mapList.get(j).get(value)
                                                    .equals(type)){
                                                if (j == i && name.equals(value)){
                                                    throw new Type1Exception();
                                                }else if (initializeVerifier.get(j).get(value)
                                                        .equals(INITIALIZED)){
                                                    flag2 = true;
                                                }
                                            }
                                        }
                                        if(!flag2){
                                            throw new Type1Exception();
                                        }
                                    }
                                    break;
                                case INT_TYPE:
                                    valueMatcher = intPattern.matcher(value);
                                    if (!valueMatcher.find()) {
                                        for (int j = mapList.size()-1; j >= FINAL; j--) {
                                            if(mapList.get(j) == null){
                                                j--;
                                            }
                                            if(mapList.get(j).containsKey(value) && mapList.get(j)
                                                    .get(value).equals(type)){
                                                if (j == i && name.equals(value)){
                                                    throw new Type1Exception();
                                                }else if (initializeVerifier.get(j).get(value)
                                                        .equals(INITIALIZED)){
                                                    flag2 = true;
                                                }
                                            }
                                        }
                                        if (!flag2){
                                            throw new Type1Exception();
                                        }
                                    }
                                    break;
                                case DOUBLE_TYPE:
                                    valueMatcher = doublePattern.matcher(value);
                                    if (!valueMatcher.find()) {
                                        for (int j = mapList.size()-1; j >= FINAL; j--) {
                                            if(mapList.get(j) == null){
                                                j--;
                                            }
                                            if(mapList.get(j).containsKey(value) && mapList.get(j)
                                                    .get(value).equals(type)){
                                                if (j == i && name.equals(value)){
                                                    throw new Type1Exception();
                                                }else if (initializeVerifier.get(j).get(value)
                                                        .equals(INITIALIZED)){
                                                    flag2 = true;
                                                }
                                            }
                                        }
                                        if (!flag2){
                                            throw new Type1Exception();
                                        }
                                    }
                                    break;
                                case CHAR_TYPE:
                                    valueMatcher = charPattern.matcher(value);
                                    if (!valueMatcher.find()) {
                                        for (int j = mapList.size()-1; j >= FINAL; j--) {
                                            if(mapList.get(j) == null){
                                                j--;
                                            }
                                            if(mapList.get(j).containsKey(value) && mapList.get(j)
                                                    .get(value).equals(type)){
                                                if (j == i && name.equals(value)){
                                                    throw new Type1Exception();
                                                }else if (initializeVerifier.get(j).get(value)
                                                        .equals(INITIALIZED)){
                                                    flag2 = true;
                                                }
                                            }
                                        }
                                        if (!flag2){
                                            throw new Type1Exception();
                                        }
                                    }
                                    break;
                                case BOOLEAN_TYPE:
                                    valueMatcher = booleanPattern.matcher(value);
                                    if (!valueMatcher.find()) {
                                        for (int j = mapList.size()-1; j >= FINAL; j--) {
                                            if(mapList.get(j) == null){
                                                j--;
                                            }
                                            if (mapList.get(j).containsKey(value) && (mapList.get(j)
                                                    .get(value).equals(type) ||
                                                    mapList.get(j).get(value).equals(INT_TYPE) ||
                                                    mapList.get(j).get(value).equals(DOUBLE_TYPE))){
                                                flag2 = true;
                                            }
                                        }
                                        if (!flag2){
                                            throw new Type1Exception();
                                        }
                                    }
                                    break;
                                default:
                                    throw new Type1Exception();
                            }
                            break;
                        }
                    }
                    if(!flag){
                        throw new Type1Exception();
                    }
                }
                else{
                    continue;
                }
            }
        }else if (variableAssignmentMatcher.find()){
            split = line.trim().split(SPLIT_BY_EQUAL);
            String name = split[0].trim();
            String value = split[1].replace(END_LINE_SUFFIX,EMPTY_STRING).trim();
            flag = false;
            boolean flag2 = false;
            for(int i = mapList.size()-1; i>=GLOBAL; i--) {
                if (mapList.get(i).containsKey(name)) {
                    flag = true;
                    type = mapList.get(i).get(name);
                    switch (type) {
                        case STRING_TYPE:
                            valueMatcher = stringPattern.matcher(value);
                            if (!valueMatcher.find()) {
                                for (int j = mapList.size()-1; j >= FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value)
                                            .equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if(!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case INT_TYPE:
                            valueMatcher = intPattern.matcher(value);
                            if (!valueMatcher.find()) {
                                for (int j = mapList.size()-1; j >= FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value)
                                            .equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case DOUBLE_TYPE:
                            valueMatcher = doublePattern.matcher(value);
                            if (!valueMatcher.find()) {
                                for (int j = mapList.size()-1; j >= FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value)
                                            .equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case CHAR_TYPE:
                            valueMatcher = charPattern.matcher(value);
                            if (!valueMatcher.find()) {
                                for (int j = mapList.size()-1; j >= FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if(mapList.get(j).containsKey(value) && mapList.get(j).get(value)
                                            .equals(type)){
                                        if (j == i && name.equals(value)){
                                            throw new Type1Exception();
                                        }else if (initializeVerifier.get(j).get(value).equals(INITIALIZED)){
                                            flag2 = true;
                                        }
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        case BOOLEAN_TYPE:
                            valueMatcher = booleanPattern.matcher(value);
                            if (!valueMatcher.find()) {
                                for (int j = mapList.size()-1; j >= FINAL; j--) {
                                    if(mapList.get(j) == null){
                                        j--;
                                    }
                                    if (mapList.get(j).containsKey(value) && (mapList.get(j)
                                            .get(value).equals(type) ||
                                            mapList.get(j).get(value).equals(INT_TYPE) ||
                                            mapList.get(j).get(value).equals(DOUBLE_TYPE))){
                                        flag2 = true;
                                    }
                                }
                                if (!flag2){
                                    throw new Type1Exception();
                                }
                            }
                            break;
                        default:
                            throw new Type1Exception();
                    }
                    break;
                }
            }
            if(!flag){
                throw new Type1Exception();
            }
        }else if (!variableDeclarationMatcher.find()){
            throw new Type1Exception();
        }
    }
}
