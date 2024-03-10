@Library('shared-library') _

import com.lesfurets.jenkins.unit.global.lib.Library

@Library('nextlayerci-example@master') _

import io.nextlayer.ci.UtilExample

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
            sh 'mvn test'
            sayHello()
              UtilExample util = new UtilExample()
              echo util.doSomething()
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
