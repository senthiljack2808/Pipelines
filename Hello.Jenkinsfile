@Library('shared-library') _


def gv
pipeline {


    agent any
    
   tools {
           maven '3.8.4'
       }

       
    stages {
    
        stage('First Stage') {
            steps {
                echo 'This is the first stage'
            sh 'pwd'
            sh 'ls'
            sh 'mvn --version'
            sh 'java -version'
            sh 'cd target/test-classes/nextlayerci-example@master/src/io/nextlayer/ci;ls'
            sayHello()
            echo 'end'
            }
        }
        
        
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
    }
}
