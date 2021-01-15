package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that does deeps copy on complex mutable data structures we use.
 */
public class MapsDeepCopy {
    private ArrayList<Map<String, String>> mapsArrayToCopy;

    /**
     * Constructor of the Maps Deep Copy class.
     * @param mapsArrayToCopy Array of maps that need to be copied.
     */
    public MapsDeepCopy(ArrayList<Map<String, String>> mapsArrayToCopy) {
        this.mapsArrayToCopy = mapsArrayToCopy;
    }

    /**
     * Makes a deep copy of the data structure.
     * @return Copied array.
     */
    public ArrayList<Map<String, String>> deepCopy(){
        ArrayList<Map<String, String>> copiedMapsArray = new ArrayList<>();
        for(Map<String, String>map: mapsArrayToCopy){
            Map<String,String> copy = new HashMap<>();
            for(Map.Entry<String,String> entry:map.entrySet()){
                copy.put(entry.getKey(), entry.getValue());
            }
            copiedMapsArray.add(copy);
        }
        return copiedMapsArray;
    }
}
