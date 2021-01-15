package oop.ex6.main;

import java.io.IOException;

/**
 * Class that extends IOException, thrown when an illegal file was given.
 */
public class Type2Exception extends IOException {

    Type2Exception(){
        super();
    }

    Type2Exception(String message){
        super(message);
    }
}
