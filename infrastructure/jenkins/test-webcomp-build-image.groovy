pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "webcompbuild"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/webcompbuild'
        DOCKER_TAG = "test-$BUILD_NUMBER"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    rm -f .env
                    echo 'COMPOSE_PROJECT_NAME=${DOCKER_PROJECT_NAME}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env
                """
            }
        }
        stage('Test') {
            steps {
                sh '''
                    docker network create authentication || true
                    docker-compose --no-ansi build --pull --build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)
                    docker-compose --no-ansi run --rm --no-deps -u $(id -u jenkins):$(id -g jenkins) app node -v
                '''
            }
        }
        stage('Build & Push') {
            steps {
                sh '''
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker/docker-compose.build.yml push
                '''
            }
        }
    }
}
