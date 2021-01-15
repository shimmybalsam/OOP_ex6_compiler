package oop.ex6.main;

/**
 * Class that thrown when an illegal line is found in the sjavac code, has a message of 1 (to be printed)
 */
public class Type1Exception extends Exception {

    static final String message = "1";

    Type1Exception(){super(message);}
}
