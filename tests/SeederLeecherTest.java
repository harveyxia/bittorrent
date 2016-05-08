package tests;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by marvin on 5/8/16.
 */
public class SeederLeecherTest {

    @Test
    public void testSingleSeederSingleLeecher() throws IOException {

        ProcessBuilder pb = new ProcessBuilder("java","tracker/Tracker","6789")
                    .directory(new File("../"));
        Process p = pb.start();

        String line;
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
        input.close();
    }
}
