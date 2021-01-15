package oop.ex6.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Created by Shimmy on 6/14/2017.
 */
public class Sjavac {
    private static final int SUCCESS = 0;
    private static final String SPLIT_BY_DOT = "\\.";
    private static final String WARNING_MASSAGE = "Warning: Bad file name\n";

    public static void main(String[] args) throws IOException, Type1Exception, Type2Exception {
        Sjavac sj = new Sjavac();
        String filePath = args[0];
        File command = new File(filePath);
        String[] splitArg = filePath.split(SPLIT_BY_DOT);
        try{
            if(splitArg.length == 2) {
                try {
                    List<String> allLines = Files.readAllLines(command.toPath());
                    new CheckList(allLines).check();
                    System.out.println(SUCCESS);
                } catch (IOException e) {
                    filePath = splitArg[0];
                    try{
                        command = new File(filePath);
                        List<String> allLines = Files.readAllLines(command.toPath());
                        new CheckList(allLines).check();
                        System.out.println(WARNING_MASSAGE);
                        System.out.println(SUCCESS);
                    }catch (IOException t){
                        throw new Error();
                    }catch (Type1Exception t){
                        System.out.println(WARNING_MASSAGE);
                        System.out.println(t.getMessage());
                    }
                } catch (Type1Exception t) {
                    System.out.println(t.getMessage());
                }
            }
            else{
                throw new Error();
            }
        }catch (Error t){
            System.out.println(t.getMessage());
        }
    }
}