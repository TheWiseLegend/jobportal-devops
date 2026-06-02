package pipelines.javaApp

class JavaAppFeature implements Serializable {

    def script

    JavaAppFeature(def script) {
        this.script = script
    }

    void run(Map params) {
        String imageName = params.imageName
        String imageTag  = script.env.BUILD_NUMBER
        String fullImage = "${imageName}:${imageTag}"

        checkout()
        buildImage(fullImage)
    }

    private void checkout() {
        script.dir('jobportal') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal.git',
                       branch: script.env.BRANCH_NAME
        }
        script.dir('jobportal-devops') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal-devops.git',
                       branch: 'main'
        }
    }

    private void buildImage(String fullImage) {
        script.buildDockerImage(
            fullImage,
            "${script.env.WORKSPACE}/jobportal-devops/infrastructure/docker/Dockerfile",
            "${script.env.WORKSPACE}/jobportal"
        )
    }
}
