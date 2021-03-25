pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "webcompbuild"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/webcompbuild'
        DOCKER_TAG = "latest"

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
                sh '''
					rm -rf .env
					echo 'DB_USER=$DB_USER' >> .env
					echo 'DB_PASS=$DB_PASS' >> .env
				'''

                sh """
					echo 'DB_HOST=$DB_HOST' >> .env
					echo 'DB_PORT=$DB_PORT' >> .env
					echo 'GITHUB_ORGANIZATION=$GITHUB_ORGANIZATION' >> .env
					echo 'GITHUB_ORIGINS_REPO=$GITHUB_ORIGINS_REPO' >> .env
					echo 'GITHUB_ORIGINS_BRANCH=$GITHUB_ORIGINS_BRANCH' >> .env
					echo 'GITHUB_ORIGINS_FILE=$GITHUB_ORIGINS_FILE' >> .env

                    mkdir -p ~/.ssh
                    ssh-keyscan -H $SSH_CDN_ADDR >> ~/.ssh/known_hosts
					ssh-keyscan -H github.com >> ~/.ssh/known_hosts
                    echo 'Host tomcattest2' >> ~/.ssh/config
                    echo '  User $SSH_CDN_USER' >> ~/.ssh/config
                    echo '  Hostname $SSH_CDN_ADDR' >> ~/.ssh/config

					git config --global user.email "info@opendatahub.bz.it"
					git config --global user.name "Jenkins"
					git remote set-url origin $GIT_URL
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
