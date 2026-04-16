/**
 * Pushes a Docker image to Docker Hub.
 *
 * @param imageName   Full image name with tag, e.g. "amrthedev/jobportal:5"
 * @param credentialsId  Jenkins credential ID for Docker Hub login
 */
def call(String imageName, String credentialsId) {
    echo "Pushing Docker image: ${imageName}"

    // docker.withRegistry logs in to the registry using the stored Jenkins credential,
    // runs the closure block, then logs out automatically.
    // First argument: registry URL — Docker Hub's official API endpoint.
    // Second argument: the credential ID you created in Jenkins UI.
    docker.withRegistry('https://registry-1.docker.io/v2/', credentialsId) {

        // Push with the build number tag (e.g. :5) for traceability
        docker.image(imageName).push()

        // Also push as :latest so docker-compose always pulls the newest image
        // by default without specifying a tag
        docker.image(imageName).push('latest')
    }
}
