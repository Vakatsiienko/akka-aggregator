package com.akka.testProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Iaroslav on 8/18/2016.
 */
public class TestFileGeneratorUtil {
    public static void generateFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int i = 1; i <= 100000; i++) {
            Long id = Math.round(Math.random() * 999 + 1);
            BigDecimal value = new BigDecimal(Math.random() * 10000).setScale(4, BigDecimal.ROUND_HALF_UP);
            writer.write(id + ";" + value.doubleValue() + "\n");
        }
        writer.close();
    }

}
