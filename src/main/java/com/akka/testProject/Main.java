package com.akka.testProject;


import java.io.IOException;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        TestFileGeneratorUtil.generateFile(args[0]);
        new DataService().aggregateData(args[0], args[1], Integer.valueOf(args[2]), Long.valueOf(args[3]));
    }
}
