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
            sh 'mvn clean install'
            sh 'mvn test'
            sh 'java -version'
            sh 'cd target;ls'
            sayHello()
            echo 'end'
            }
        }
        
        stage('Second Stage') {
            steps {
                echo 'This is the second stage'
                  script{
                            gv =load "test/groovy/HelloPipeline.groovy"
                          }
                echo 'end'
            }
        }
        
            stage('third last Stage') {
                    steps {
                    script{
                                 gv.execute()
                            }
                        echo 'This is the third senthil stage'
                        
                    }
                }
                
                     stage('fourth Stage') {
                                    steps {
                                    
                                        echo 'This is the fourth stage'
                                        
                                    }
                                }
       
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
    }
}
