pipeline {
    agent {
        dockerfile {
            filename 'infrastructure/docker/webcomp-deploy.dockerfile'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)'
        }
    }

    options {
        ansiColor('xterm')
    }

    environment {
        DB_USER = credentials('webcompstore-test-postgres-username')
        DB_PASS = credentials('webcompstore-test-postgres-password')
        SSH_CDN_ADDR = "34.253.82.250"
        SSH_CDN_USER = "admin"
    }

    parameters {
        string(
            name: 'WC_NAME', 
            defaultValue: 'webcomp-mobility-traffic', 
            description: 'Web component git repository name',
            trim: true
        )
        string(
            name: 'WC_TAG', 
            defaultValue: 'v0.2.1', 
            description: 'Web component git tag',
            trim: true
        )
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    cd utils
					echo 'DB_HOST=test-pg-bdp.co90ybcr8iim.eu-west-1.rds.amazonaws.com' > .env
					echo 'DB_PORT=5432' >> .env
					echo 'DB_USER=$DB_USER' >> .env
					echo 'DB_PASS=$DB_PASS' >> .env
					echo 'WC_NAME=$WC_NAME' >> .env
					echo 'WC_TAG=$WC_TAG' >> .env

                    mkdir -p ~/.ssh
                    ssh-keyscan -H $SSH_CDN_ADDR >> ~/.ssh/known_hosts
                    echo 'Host tomcattest2' >> ~/.ssh/config
                    echo '  User $SSH_CDN_USER' >> ~/.ssh/config
                    echo '  Hostname $SSH_CDN_ADDR' >> ~/.ssh/config
				"""
            }
        }
        stage('Deploy') {
            steps {
                sshagent (credentials: ['tomcatkey']) {
                    sh """
                        cd utils
                        ./deploy-webcomp.sh
                    """
                }
            }
        }
    }
}
