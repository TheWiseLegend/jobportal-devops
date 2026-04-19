// This script is executed by the Job DSL plugin.
// It programmatically creates the jobportal-pipeline job in Jenkins.
// Run this once (or on any Jenkins rebuild) to restore the pipeline without UI clicking.

pipelineJob('jobportal-pipeline') {

    description('CI/CD pipeline for the Job Portal Spring Boot monolith — builds and pushes Docker image to Docker Hub.')

    // Discard old builds to avoid filling disk — keep last 10
    logRotator {
        numToKeep(10)
    }

    // Trigger this job automatically when GitHub pushes a webhook
    triggers {
        githubPush()
    }

    definition {
        // Pull the Jenkinsfile from SCM (jobportal-devops repo)
        // instead of embedding pipeline code here — keeps concerns separated
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/TheWiseLegend/jobportal-devops.git')
                    }
                    branch('*/main')
                }
            }
            // Path to the Jenkinsfile inside jobportal-devops
            scriptPath('jenkins/pipelines/job-portal/Jenkinsfile')

            // Do not allow users to edit pipeline definition from the Jenkins UI
            lightweight(true)
        }
    }
}
