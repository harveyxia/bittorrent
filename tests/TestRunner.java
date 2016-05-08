package tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        Class[] classes = {MetaFileUtilsTest.class, MessageParserTest.class,
                MessageBuilderTest.class, TrackerTest.class, SeederLeecherTest.class};

        System.out.println("\n==================");
        System.out.println("    begin test    ");
        System.out.println("==================\n");

        Result result = JUnitCore.runClasses(classes);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println("\n==================");
        if (result.wasSuccessful())
            System.out.println(" tests successful ");
        else
            System.out.println(" tests failed.... ");
        System.out.println("==================\n");
    }
}
