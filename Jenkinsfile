pipeline {
    agent any

    options {
        timestamps()
        skipDefaultCheckout(true)
    }

    environment {
        APP_NAME = 'demo-springboot-app'
        IMAGE_NAME = 'demo-springboot'
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'demo-ci-cd-springboot'
    }

    stages {
        stage('Obtener codigo') {
            steps {
                script {
                    try {
                        checkout scm
                        echo 'Codigo obtenido desde SCM.'
                    } catch (Exception e) {
                        echo 'SCM no configurado. Usando fuentes montadas para demo local.'
                        sh '''
                          rm -rf app ci docs
                          cp -R /workspace/ci-cd-springboot-nivel2/app .
                          cp -R /workspace/ci-cd-springboot-nivel2/ci .
                          cp -R /workspace/ci-cd-springboot-nivel2/docs .
                        '''
                    }
                }
            }
        }

        stage('Compilar, probar y generar cobertura') {
            steps {
                dir('app') {
                    sh 'mvn -B clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'app/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'app/target/site/jacoco/**, app/target/*.jar', allowEmptyArchive: true
                }
            }
        }

        stage('Analisis SonarQube') {
            steps {
                dir('app') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                          mvn -B sonar:sonar \
                            -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                            -Dsonar.projectName=$SONAR_PROJECT_KEY \
                            -Dsonar.host.url=$SONAR_HOST_URL \
                            -Dsonar.token=$SONAR_TOKEN \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }
            }
        }

        stage('Validar Quality Gate') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                      chmod +x ci/wait-for-sonar-quality-gate.sh
                      ci/wait-for-sonar-quality-gate.sh \
                        "$SONAR_HOST_URL" \
                        "$SONAR_TOKEN" \
                        "app/target/sonar/report-task.txt"
                    '''
                }
            }
        }

        stage('Construir imagen Docker') {
            steps {
                sh '''
                  docker build \
                    -t $IMAGE_NAME:$BUILD_NUMBER \
                    -t $IMAGE_NAME:latest \
                    app
                '''
            }
        }

        stage('Desplegar aplicativo') {
            steps {
                sh '''
                  docker rm -f $APP_NAME || true
                  docker run -d \
                    --name $APP_NAME \
                    --network cicd-net \
                    -p 8081:8080 \
                    $IMAGE_NAME:latest
                '''
            }
        }

        stage('Smoke test') {
            steps {
                sh '''
                  sleep 8
                  curl -f http://host.docker.internal:8081/actuator/health || \
                  curl -f http://demo-springboot-app:8080/actuator/health
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline CI/CD Nivel 2 finalizado correctamente.'
        }
        failure {
            echo 'Pipeline fallido. Revisar logs de Jenkins y resultado de SonarQube.'
        }
    }
}
