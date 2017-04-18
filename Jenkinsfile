pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh '/usr/local/gradle/bin/gradle build -x test'
      }
    }
  }
}