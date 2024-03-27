#!/usr/bin/env groovy
package io.nextlayer.ci

//@GrabResolver(name='solocode-1.0-SNAPSHOT.jar', root='.jenkins/workspace/pipelineTrial')
@Grab('org.example:solocode:1.0-SNAPSHOT')

import org.example.App;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

class UtilExample implements Serializable {

    public UtilExample() {
        
    }
    
    String doSomething() {
        
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now()

        // Define a custom date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Format the current date and time using the formatter
        String formattedDateTime = currentDateTime.format(formatter)

        // Print the formatted date and time
        // "Something done Formatted Date and Time: $formattedDateTime"
        String str = "Hello, world!";
        
        // Check if the string is empty
        if (StringUtils.isEmpty(str)) {
            return "The string is empty";
        } else {
            def run=new App().run();
            return "The string is not empty $run";
        }
    }
}
