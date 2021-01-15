package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class of all the check types.
 */
abstract public class Checker {
    protected static final int FINAL = 0;
    protected static final int GLOBAL = 1;
    public abstract void check() throws Type1Exception;
}
