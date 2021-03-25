pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "webcompbuild"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/webcompbuild'
        DOCKER_TAG = "test-$BUILD_NUMBER"

		DB_HOST = "test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com"
		DB_PORT = "5432"
        DB_USER = credentials('webcompstore-test-postgres-username')
        DB_PASS = credentials('webcompstore-test-postgres-password')
        SSH_CDN_ADDR = "172.31.37.40"
        SSH_CDN_USER = "admin"
		GITHUB_ORGANIZATION = "noi-techpark"
		GITHUB_ORIGINS_REPO = "odh-web-components-store-origins"
		GITHUB_ORIGINS_BRANCH = "development"
		GITHUB_ORIGINS_FILE = "origins.json"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    env | sort > .env
                """
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
