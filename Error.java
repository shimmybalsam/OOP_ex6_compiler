package oop.ex6.main;

/**
 * Class that extends Type2Exception, has a message of 2 (to be printed).
 */
public class Error extends Type2Exception {
    static final String message = "2";

    Error(){
        super(message);
    }
}
