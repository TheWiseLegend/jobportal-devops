// vars/pipelines.groovy

// Router only. No pipeline structure lives here.
// Reads the branch name and hands execution to the correct class.
// The class defines all stages through the 'script' reference it receives.


def javaApp(Map config = [:]) {
    // env.BRANCH_NAME → set automatically by Multibranch Pipeline jobs
    // env.GIT_BRANCH  → set by regular Pipeline jobs, often prefixed with 'origin/'
    // We normalise both into a clean branch name.

    def branchName = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '') ?: 'unknown'

    echo "Branch detected: ${branchName}"

    if (branchName == 'main') {
        new pipeline.javaApp.JavaAppMain(this).run(config)
    } else {
        new pipeline.javaApp.JavaAppFeature(this).run(config)
    }

}

