#!/usr/bin/env groovy

pipeline {
    agent {
        label 'linux&&jdk8'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build/Test') {
            steps {
                sh './gradlew clean build buildOSPackages --no-daemon'
            }
            post {
                always {
                    junit 'build/test-results/test/TEST-*.xml'
                }
            }
        }
        stage('Deploy') {
            steps {
                sshagent(['jenkins-repo']) {
                    sh './gradlew deploy --no-daemon --debug --stacktrace'
                }
            }
        }
    }
}
