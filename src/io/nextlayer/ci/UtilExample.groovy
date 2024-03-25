#!/usr/bin/env groovy
package io.nextlayer.ci

import org.example.App

class UtilExample implements Serializable {

    public UtilExample() {
    }
    
    String doSomething() {
        new App().run();
        return "Something done"
    }
}
