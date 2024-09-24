pipeline {
    agent any
    environment {
        IMAGE_NAME = 'lodgy-server'
        DOCKER_REGISTRY = 'docker.local'
        STACK_NAME = 'lodgy'
        REPO_URL_BACKEND = 'git@github.com:carnivalgoblin/lodgy-server.git'
        REPO_URL_COMPOSE = 'git@github.com:carnivalgoblin/docker-compose.git'
        REPO_BRANCH_BACKEND = 'master' // Change as necessary
        REPO_BRANCH_COMPOSE = 'main' // Change as necessary
        ENDPOINT_ID = '2'
    }

    stages {
        stage('Checkout Backend') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "*/${REPO_BRANCH_BACKEND}"]],
                    userRemoteConfigs: [[url: "${REPO_URL_BACKEND}", credentialsId: 'gh']]
                ])
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    sh 'chmod +x ./mvnw'
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
    }


    post {
        success {
            echo 'Backend image build was successful!'
        }
        failure {
            echo 'Backend image build failed.'
        }
    }
}
