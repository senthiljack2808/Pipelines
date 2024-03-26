package io.nextlayer.ci
import static org.junit.Assert.assertEquals

public class App {
    public App() {
    }

    public void run() {
        println "Hello world!"
        assertEquals(1, 1)
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
