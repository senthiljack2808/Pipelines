#!/usr/bin/env groovy
package io.nextlayer.ci

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        return "Something done Formatted Date and Time: $formattedDateTime"
    }
}
