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
                def summaryPage = load "${WORKSPACE}/HelloPipeline.groovy"
                summaryPage.getSummary()
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
