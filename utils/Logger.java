package utils;

/**
 * Created by marvin on 5/5/16.
 */
public class Logger {

    private String name;

    public Logger(String name) {
        this.name = name;
    }

    public void log(String s) {
        System.out.println("Client " + name + ": " + s);
    }
}
