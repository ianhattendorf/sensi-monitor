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
    }
}
