@Library('pro_utils')

pipeline {
    agent any
    environment {
        IMAGE_NAME = 'lodgy-server'
        DOCKER_REGISTRY = 'docker.local'
        PORTAINER_URL = 'http://192.168.86.31:9000/api'
        STACK_NAME = 'lodgy'
        STACK_FILE_PATH = 'lodgy.yml'
        REPO_URL = 'git@github.com:carnivalgoblin/docker-compose.git'
        REPO_BRANCH = 'master'
        ENDPOINT_ID = '2'
    }

    stages {
        stage('Checkout Stack File') {
            steps {
                script {
                    checkout([$class: 'GitSCM',
                        branches: [[name: "*/${REPO_BRANCH}"]],
                        userRemoteConfigs: [[url: "${REPO_URL}", credentialsId: 'gh']]
                    ])
                }
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    sh 'chmod +x ./mvnw'  // Make the mvnw script executable
                    sh './mvnw clean package'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    buildDockerImage("${DOCKER_REGISTRY}/${IMAGE_NAME}")
                }
            }
        }

        stage('Deploy Backend Stack') {
            steps {
                script {
                    def apiKey = credentials('portainer-api-key')
                    portainerLib.deployStack("${WORKSPACE}/${STACK_FILE_PATH}", STACK_NAME, PORTAINER_URL, apiKey, ENDPOINT_ID)
                }
            }
        }
    }

    post {
        success {
            echo 'Backend deployment was successful!'
        }
        failure {
            echo 'Backend deployment failed.'
        }
    }
}
