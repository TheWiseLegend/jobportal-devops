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

    // BranchDiscoveryTrait has no native DSL binding.
    // The << operator with a Closure evaluates in NodeBuilder context —
    // inside the closure, 'TagName'() creates a child XML element.
    // This matches the exact XML Jenkins generates when you add
    // "Discover branches" via the UI (confirmed from config.xml).
    configure {
        it / sources / data / 'jenkins.branch.BranchSource' / source / traits << {
            'jenkins.plugins.git.traits.BranchDiscoveryTrait'()
        }
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
