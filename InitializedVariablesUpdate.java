package oop.ex6.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that checks for all the variables that were declared and initialized.
 */
public class InitializedVariablesUpdate {
    private static final String INITIALIZED = "Initialized";

    private ArrayList<Map<String, String>> mapsList;
    private ArrayList<Map<String, String>> initialized;

    /**
     * Constructor of the Initialized Variables Update class.
     * @param mapsList List of all the maps of variables so far.
     * @param initialized List of all the maps of the variables that where initialized so far.
     */
    public InitializedVariablesUpdate(ArrayList<Map<String, String>> mapsList,
                                      ArrayList<Map<String, String>> initialized) {

        this.mapsList = mapsList;
        this.initialized = initialized;
    }

    /**
     * Creates new array that contains maps of the valid variables that where also initialized so far the in
     * the sjava file. the update happens in a way that keeps the scopes order in which the variables where
     * declared and initialized.
     * @return updated array.
     */
    public ArrayList<Map<String, String>> update(){
        ArrayList<Map<String, String>> updated = new ArrayList<>();
        for (int i = 0; i < initialized.size(); i++){
            Map<String, String> temp = new HashMap<>();
            for (String key: mapsList.get(i).keySet()){
                if (initialized.get(i).containsKey(key)){
                    if(initialized.get(i).get(key).equals(INITIALIZED)) {
                        temp.put(key, mapsList.get(i).get(key));
                    }
                }
            }
            updated.add(temp);
        }
        return updated;
    }
}
