package oop.ex6.main;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class that makes the needed checks for scope.
 */
public class ScopesCheckMaker {
    final static private String CHECK_VARIABLES = "Variables";
    final static private String CHECK_IF_WHILE = "If/While";
    final static private String CHECK_METHOD_CALL = "Method call";

    Map<String, String> localMap;
    Map<String, String> localInitializer;
    ArrayList<Map<String, String>> allMapsList;
    ArrayList<Map<String, String>> initializeVerifier;
    int depthCounter;
    ArrayList<String> subScope;
    Map<String, ArrayList<Map<String, String>>> methodCalls;
    ArrayList<Map<String, String>> updatedAndVerifiedMaps;
    boolean localMapCreated;
    String check;
    String line;

    /**
     * First constructor for the Scopes Check maker class, used if the checking is not for a specific line.
     * @param localMap Map that contains the variables inside the scope.
     * @param localInitializer Map contains the local variables that were initialized.
     * @param allMapsList Array that contains all the variables maps.
     * @param initializeVerifier Array that contains all the maps of the variables that were initialized so far.
     * @param depthCounter the depth of the block in the code.
     * @param subScope array of the inner scope, method block or If/While block.
     * @param methodCalls map containing all the calls for methods inside a scope, and array of the maps
     *                    containing all the variables that were declared and initialized so far.
     * @param updatedAndVerifiedMaps Array of maps that contains all the variables that were verified and
     *                               initialized.
     * @param localMapCreated true if local variables map was created, false otherwise.
     * @param check the check that is needed.
     */
    public ScopesCheckMaker(Map<String, String> localMap, Map<String, String> localInitializer,
                            ArrayList<Map<String, String>> allMapsList,
                            ArrayList<Map<String, String>> initializeVerifier, int depthCounter,
                            ArrayList<String> subScope, Map<String, ArrayList<Map<String, String>>>
                                    methodCalls, ArrayList<Map<String, String>> updatedAndVerifiedMaps,
                            boolean localMapCreated, String check) {

        this.localMap = localMap;
        this.localInitializer = localInitializer;
        this.allMapsList = allMapsList;
        this.initializeVerifier = initializeVerifier;
        this.depthCounter = depthCounter;
        this.subScope = subScope;
        this.methodCalls = methodCalls;
        this.updatedAndVerifiedMaps = updatedAndVerifiedMaps;
        this.localMapCreated = localMapCreated;
        this.check = check;
    }

    /**
     * Second constructor for the Scopes Check maker class, used if the checking is for a specific line.
     * @param localMap Map that contains the variables inside the scope.
     * @param localInitializer Map contains the local variables that were initialized.
     * @param allMapsList Array that contains all the variables maps.
     * @param initializeVerifier Array that contains all the maps of the variables that were initialized so far.
     * @param depthCounter the depth of the block in the code.
     * @param subScope array of the inner scope, method block or If/While block.
     * @param methodCalls map containing all the calls for methods inside a scope, and array of the maps
     *                    containing all the variables that were declared and initialized so far.
     * @param updatedAndVerifiedMaps Array of maps that contains all the variables that were verified and
     *                               initialized.
     * @param localMapCreated true if local variables map was created, false otherwise.
     * @param check the check that is needed.
     * @param line The line for the check.
     */
    public ScopesCheckMaker(Map<String, String> localMap, Map<String, String> localInitializer,
                            ArrayList<Map<String, String>> allMapsList,
                            ArrayList<Map<String, String>> initializeVerifier, int depthCounter,
                            ArrayList<String> subScope, Map<String, ArrayList<Map<String, String>>>
                                    methodCalls, ArrayList<Map<String, String>> updatedAndVerifiedMaps,
                            boolean localMapCreated, String check, String line) {

        this.localMap = localMap;
        this.localInitializer = localInitializer;
        this.allMapsList = allMapsList;
        this.initializeVerifier = initializeVerifier;
        this.depthCounter = depthCounter;
        this.subScope = subScope;
        this.methodCalls = methodCalls;
        this.updatedAndVerifiedMaps = updatedAndVerifiedMaps;
        this.localMapCreated = localMapCreated;
        this.check = check;
        this.line = line;
    }

    /**
     * Makes the relevant check.
     * @throws Type1Exception If something was invalid.
     */
    public void checkMaker() throws Type1Exception {
        ArrayList<Map<String, String>> copiedMapsArray = new MapsDeepCopy(allMapsList).deepCopy();
        ArrayList<Map<String, String>> copiedInitializerArray = new MapsDeepCopy(initializeVerifier).deepCopy();
        copiedMapsArray.add(localMap);
        copiedInitializerArray.add(localInitializer);
        try {
            switch (check) {
                case CHECK_IF_WHILE:
                    if (localMapCreated || localMap.size() > 0) {
                        new CheckIfWhile(subScope, depthCounter, copiedMapsArray,
                                copiedInitializerArray, methodCalls).check();
                    }else{
                        new CheckIfWhile(subScope, depthCounter, allMapsList, initializeVerifier,
                                methodCalls).check();
                    }
                    break;
                case CHECK_VARIABLES:
                    if (localMapCreated || localMap.size() > 0) {
                        new CheckVariables(line,copiedMapsArray, copiedInitializerArray).check();
                    }
                    else{
                        new CheckVariables(line, allMapsList, initializeVerifier).check();
                    }
                    break;
                case CHECK_METHOD_CALL:
                    if (localMapCreated || localMap.size() > 0) {
                        updatedAndVerifiedMaps = new InitializedVariablesUpdate(copiedMapsArray,
                                copiedInitializerArray).update();
                        methodCalls.put(line.trim(), updatedAndVerifiedMaps);
                    }else{
                        updatedAndVerifiedMaps = new InitializedVariablesUpdate(allMapsList,
                                initializeVerifier).update();
                        methodCalls.put(line.trim(), updatedAndVerifiedMaps);
                    }
                    break;
            }
        }catch (Type1Exception t){
            throw new Type1Exception();
        }
    }
}
