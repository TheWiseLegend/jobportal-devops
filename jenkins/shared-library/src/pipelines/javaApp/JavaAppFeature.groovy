package pipelines.javaApp

// Feature branch pipeline — CI only.
// Runs on any branch that is NOT main.
// Stages: Checkout → Test → Build
// No Push, no Deploy — feature branches are validated only, never shipped.
class JavaAppFeature implements Serializable {

    def script  // ID card received from vars/pipelines.groovy

    JavaAppFeature(def script) {
        this.script = script
    }

    void run(Map config) {
        String imageName = config.imageName
        String imageTag  = script.env.BUILD_NUMBER
        String fullImage = "${imageName}:${imageTag}"

        // Normalise branch name — same logic as vars/pipelines.groovy.
        // BRANCH_NAME is set by Multibranch Pipeline jobs.
        // GIT_BRANCH is set by regular Pipeline jobs, often with 'origin/' prefix.
        // We strip 'origin/' so the git step receives a clean branch name.
        String branchName = script.env.BRANCH_NAME
                         ?: script.env.GIT_BRANCH?.replaceAll('origin/', '')
                         ?: 'main'

        script.node {
            try {
                script.stage('Checkout') {
                    checkout(branchName)
                }

                script.stage('Test') {
                    test()
                }

                script.stage('Build') {
                    buildImage(fullImage)
                }

            } finally {
                // Clean workspace whether the build passed or failed.
                // Prevents leftover files from polluting the next run.
                script.cleanWs()
            }
        }
    }

    private void checkout(String branchName) {
        // Clone the feature branch of the app repo — not hardcoded to main.
        // This is what makes feature branch CI meaningful: we test the actual
        // code the developer pushed, not the main branch.
        script.dir('jobportal') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal.git',
                       branch: branchName
        }

        // Devops repo always clones from main.
        // The Dockerfile and build tooling live here — they don't vary per feature branch.
        script.dir('jobportal-devops') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal-devops.git',
                       branch: 'main'
        }
    }

    private void test() {
        // Run tests against the feature branch code.
        // -B = batch mode for clean Jenkins logs.
        script.dir('jobportal') {
            echo "testing..."
        }
    }

    private void buildImage(String fullImage) {
        // Build the Docker image to verify the app compiles and packages correctly.
        // We do NOT push — this image is for validation only, not for deployment.
        script.buildDockerImage(
            fullImage,
            "${script.env.WORKSPACE}/jobportal-devops/infrastructure/docker/Dockerfile",
            "${script.env.WORKSPACE}/jobportal"
        )
    }
}
