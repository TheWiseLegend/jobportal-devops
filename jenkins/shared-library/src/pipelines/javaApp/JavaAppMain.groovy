package pipelines.javaApp

class JavaAppMain implements Serializable {

    def script

    JavaAppMain(def script) {
        this.script = script
    }

    void run(Map params) {
        String imageName    = params.imageName
        String credentialsId = params.credentialsId
        String imageTag     = script.env.BUILD_NUMBER
        String fullImage    = "${imageName}:${imageTag}"

        checkout()
        buildImage(fullImage)
        push(fullImage, credentialsId)
    }

    private void checkout() {
        script.dir('jobportal') {
            script.git url: 'https://github.com/TheWiseLegend/jobportal.git',
                       branch: 'main'
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

    private void push(String fullImage, String credentialsId) {
        script.pushToRegistry(fullImage, credentialsId)
    }
}
