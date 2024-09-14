@Library('pro_utils') _

pipeline {
    agent any
    environment {
        IMAGE_NAME = 'lodgy-server'
        DOCKER_REGISTRY = 'docker.local'
        PORTAINER_URL = 'http://192.168.86.31:9000/api'
        STACK_NAME = 'lodgy'
        STACK_FILE_PATH = 'lodgy.yml'
        REPO_URL = 'git@github.com:carnivalgoblin/docker-compose.git'
        REPO_BRANCH = 'main'
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

        stage('Check Commit Message') {
            steps {
                script {
                    def commitMessage = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    echo "Latest Commit Message: ${commitMessage}"

                    if (commitMessage.contains('release_now')) {
                        echo 'Commit message contains "release_now". Proceeding with build.'
                        env.BUILD_ALLOWED = 'true'
                    } else {
                        echo 'Commit message does not contain "release_now". Skipping build.'
                        env.BUILD_ALLOWED = 'false'
                    }
                }
            }
        }

        stage('Build Backend') {
            when {
                expression { return env.BUILD_ALLOWED == 'true' }
            }
            steps {
                script {
                    sh './mvnw clean package'
                }
            }
        }

        stage('Build Docker Image') {
            when {
                expression { return env.BUILD_ALLOWED == 'true' }
            }
            steps {
                script {
                    buildDockerImage("${DOCKER_REGISTRY}/${IMAGE_NAME}")
                }
            }
        }

        stage('Deploy Backend Stack') {
            when {
                expression { return env.BUILD_ALLOWED == 'true' }
            }
            steps {
                script {
                    def apiKey = credentials('portainer-api-key')
                    portainerLib.updateStack("${WORKSPACE}/${STACK_FILE_PATH}", STACK_NAME, PORTAINER_URL, apiKey, ENDPOINT_ID)
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
