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
