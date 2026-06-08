package pipelines.javaApp

// Serializable is mandatory for all src/ classes in Jenkins.
// Jenkins can pause a pipeline mid-run (e.g. waiting for approval).
// Any object alive during that pause must be saveable to disk — hence Serializable.

class JavaAppMain implements Serializable {

    def script

    JavaAppMain(def script) {
        this.script = script
    }

    void run(Map config) {
        String imageName    = config.imageName
        String credentialsId = config.credentialsId

        String imageTag     = script.env.BUILD_NUMBER
        String fullImage    = "${imageName}:${imageTag}"

        script.node {
            try {
                script.stage('Checkout') {
                    checkout()
                }
                script.stage('Test') {
                    test()
                }
                script.stage('Build') {
                    buildImage(fullImage)
                }
                script.stage('Deploy') {
                    deploy()
                }

            } finally {
                // Runs whether the pipeline succeeded or failed.
                // Deletes all workspace files so the next build starts clean.
                // Prevents stale JARs or images from a previous run causing false passes.
                script.cleanWs()

            }
        }
    }

    private void checkout() {
        script.dir('jobportal') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal.git', branch: 'main'
        }
        script.dir('jobportal-devops') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal-devops.git', branch: 'main'
        }
    }

    private void test() {
        // -B = batch mode: no progress bars, cleaner logs in Jenkins.
        script.dir('jobportal') {
            script.echo "Skipping tests — MySQL not available in CI yet. Revisit in Phase 4."
        }
    }

    private void buildImage(String fullImage) {
        script.buildDockerImage(
            fullImage,
            "${script.env.WORKSPACE}/jobportal-devops/infrastructure/docker/Dockerfile",
            "${script.env.WORKSPACE}/jobportal"
        )
    }

    private void push(String fullImage, String credentialsId) {
        script.pushToRegistry(fullImage, credentialsId)
    }

    private void deploy() {
        script.echo "Deploy stage - ansible not configured yet, soon..."
    }
}
