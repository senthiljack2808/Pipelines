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
            sh 'export M2_HOME=/Applications/apache-maven-3.6.0;export PATH=$PATH:$M2_HOME/bin;echo $M2_HOME;echo $PATH;mvn --version'
            echo 'end'
            }
        }
        
        stage('Second Stage') {
            steps {
                echo 'This is the second stage'
                
                echo 'end'
            }
        }
        
            stage('third last Stage') {
                    steps {
                        echo 'This is the third senthil stage'
                        
                    }
                }
       
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
    }
}
