package io.nextlayer.ci
import io.nextlayer.ci.A;

public class App {
    public App() {
    }

     String run() {
         new A().sayHello();
        return "Hello world!"
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
