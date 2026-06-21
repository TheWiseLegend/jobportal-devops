multibranchPipelineJob('jobportal-pipeline-multibranch') {

    description('CI/CD pipeline for the Job Portal Spring Boot monolith — Multibranch.')

    branchSources {
        branchSource {
            source {
                git {
                    id('jobportal-app-source')
                    remote('https://github.com/TheWiseLegend/jobportal.git')
                    credentialsId('')
                }
            }
        }
    }

    // BranchDiscoveryTrait has no native binding in Job DSL's git source.
    // Without it, Jenkins fetches the repo but never actually scans for branches.
    // The configure block injects it directly into the underlying Jenkins XML config.
    configure {
        def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
        traits << 'jenkins.plugins.git.traits.BranchDiscoveryTrait'()
    }

    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
        }
    }

    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
        }
    }
}


