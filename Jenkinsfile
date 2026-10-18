pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'yassine68'
        IMAGE_BACKEND   = "${DOCKER_HUB_USER}/appgestionprojets-backend"
        IMAGE_FRONTEND  = "${DOCKER_HUB_USER}/appgestionprojets-frontend"
        IMAGE_TAG       = "v${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        K8S_NAMESPACE   = 'devops'
        PATH            = "/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin:${env.PATH}"
    }

    stages {

        // =====================================================
        // CI - Continuous Integration
        // =====================================================
        stage('CI-1  Get code from Git') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/yassineall8/DevOps-AppGestionDesProjets.git'
            }
        }

        stage('CI-2  Compile (mvn clean compile)') {
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

        stage('CI-4  Unit Tests (mvn test)') {
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

        stage('CI-5  Package (mvn package -DskipTests)') {
            steps {
                dir('backend/backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        // =====================================================
        // CD - Build & Push Docker images
        // =====================================================
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

        // =====================================================
        // CD - Docker Compose deployment
        // =====================================================
        stage('CD-4a  Docker Compose Down') {
            steps {
                sh 'docker compose down || true'
            }
        }

        stage('CD-4b  Docker Compose Up') {
            steps {
                sh 'docker compose up -d'
                sh 'docker compose ps'
            }
        }

        // =====================================================
        // CD - Kubernetes deployment (Minikube)
        // =====================================================
        stage('CD-5a  K8s: Ensure namespace') {
            steps {
                sh """
                    kubectl get namespace ${K8S_NAMESPACE} >/dev/null 2>&1 || \
                        kubectl create namespace ${K8S_NAMESPACE}
                """
            }
        }

        stage('CD-5b  K8s: Apply MySQL (PV + PVC + Deployment + Service)') {
            steps {
                sh 'kubectl apply -f mysql-deployment.yaml'
                sh "kubectl rollout restart deployment/mysql -n ${K8S_NAMESPACE}"
                sh "kubectl rollout status deployment/mysql -n ${K8S_NAMESPACE} --timeout=300s"
            }
        }

        stage('CD-5c  K8s: Apply Spring (ConfigMap + Secret + Deployment + Service)') {
            steps {
                sh 'kubectl apply -f spring-deployment.yaml'
                sh "kubectl rollout restart deployment/spring -n ${K8S_NAMESPACE}"
                sh "kubectl rollout status deployment/spring -n ${K8S_NAMESPACE} --timeout=300s"
            }
        }

        stage('CD-5d  K8s: Apply Frontend (Deployment + Service)') {
            steps {
                sh 'kubectl apply -f frontend-deployment.yaml'
                sh "kubectl rollout restart deployment/frontend -n ${K8S_NAMESPACE}"
                sh "kubectl rollout status deployment/frontend -n ${K8S_NAMESPACE} --timeout=300s"
            }
        }

        stage('CD-6  K8s: Verify deployment') {
            steps {
                sh "kubectl get pods -n ${K8S_NAMESPACE}"
                sh "kubectl get svc -n ${K8S_NAMESPACE}"
                sh 'kubectl get pv'
                sh "kubectl get pvc -n ${K8S_NAMESPACE}"
                sh """
                    kubectl exec -n ${K8S_NAMESPACE} deploy/spring -- \
                        wget -qO- --timeout=30 http://localhost:8080/entreprise/all || true
                """
                sh """
                    kubectl exec -n ${K8S_NAMESPACE} deploy/frontend -- \
                        wget -qO- --timeout=10 http://localhost/api/entreprise/all || true
                """
            }
        }
    }

    post {
        success {
            echo 'Pipeline finished successfully — both Docker Compose and Kubernetes are deployed.'
        }
        failure {
            echo 'Pipeline failed — check the failing stage.'
        }
    }
}
