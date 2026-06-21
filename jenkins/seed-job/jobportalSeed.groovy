// This script is executed by the Job DSL plugin.
// It programmatically creates the jobportal-pipeline-multibranch job in Jenkins.
// Multibranch Pipeline scans the app repo, finds all branches with a Jenkinsfile,
// and creates a sub-job per branch — with BRANCH_NAME set automatically on each build.

multibranchPipelineJob('jobportal-pipeline-multibranch') {

    description('CI/CD pipeline for the Job Portal Spring Boot monolith — Multibranch.')

    // Points at the APP repo, not the devops repo.
    // Jenkins scans this repo for branches that contain a Jenkinsfile and builds them.
    branchSources {
        branchSource {
            source {
                git {
                    // Unique ID for this source — must be stable across seed job runs
                    id('jobportal-app-source')
                    remote('https://github.com/TheWiseLegend/jobportal.git')
                    // Empty = no credentials needed (public repo)
                    credentialsId('')
                }
            }
        }
    }

    // Jenkinsfile is at the root of the app repo
    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
        }
    }

    // When a branch is deleted from GitHub, clean up its sub-job in Jenkins too.
    // Keep last 10 builds per branch.
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
        }
    }
}
