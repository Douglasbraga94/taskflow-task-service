pipeline {
    agent any

    stages {
        stage('Build & Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean test'
            }
        }
    }
}