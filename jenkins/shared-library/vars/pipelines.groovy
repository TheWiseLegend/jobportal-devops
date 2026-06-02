// Entry point for all pipeline templates.
// Called directly from each app's Jenkinsfile.
// Routes to the correct flow based on the current Git branch.

def javaApp(Map params = [:]) {
    pipeline {
        agent any
        stages {
            stage('Run') {
                steps {
                    script {
                        if (env.BRANCH_NAME == 'main') {
                            new pipelines.javaApp.JavaAppMain(this).run(params)
                        } else {
                            new pipelines.javaApp.JavaAppFeature(this).run(params)
                        }
                    }
                }
            }
        }
    }
}
