pipeline {

    agent any
    
    stages {
        stage('First Stage') {
            steps {
                echo 'This is the first stage'
            sh 'pwd'
            sh 'ls'
            sh 'mvn test'
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
