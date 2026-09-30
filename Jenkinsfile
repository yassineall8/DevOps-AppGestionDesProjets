pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'yassine68'
        IMAGE_BACKEND   = "${DOCKER_HUB_USER}/appgestionprojets-backend"
        IMAGE_FRONTEND  = "${DOCKER_HUB_USER}/appgestionprojets-frontend"
        IMAGE_TAG       = "v${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
    }

    stages {

        stage('CI-1  Get code from Git') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/yassineall8/DevOps-AppGestionDesProjets.git'
            }
        }

        stage('CI-2  Compile') {
            steps {
                dir('backend/backend') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('CI-3  SonarQube Analysis') {
            steps {
                dir('backend/backend') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('CI-4  Unit Tests') {
            steps {
                dir('backend/backend') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('CI-5  Package') {
            steps {
                dir('backend/backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('CD-1  Build Backend Image') {
            steps {
                dir('backend/backend') {
                    sh "docker build -t ${IMAGE_BACKEND}:${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_BACKEND}:${IMAGE_TAG} ${IMAGE_BACKEND}:latest"
                }
            }
        }

        stage('CD-2  Build Frontend Image') {
            steps {
                dir('frontend') {
                    sh "docker build -t ${IMAGE_FRONTEND}:${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_FRONTEND}:${IMAGE_TAG} ${IMAGE_FRONTEND}:latest"
                }
            }
        }

        stage('CD-3  Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh "docker push ${IMAGE_BACKEND}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_BACKEND}:latest"
                    sh "docker push ${IMAGE_FRONTEND}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_FRONTEND}:latest"
                }
            }
        }

        stage('CD-4  Docker Compose Up') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d'
                sh 'docker compose ps'
            }
        }
    }

    post {
        success { echo 'Pipeline finished successfully.' }
        failure { echo 'Pipeline failed — check logs.' }
    }
}
