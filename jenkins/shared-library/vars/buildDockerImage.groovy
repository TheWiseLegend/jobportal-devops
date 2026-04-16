/**
 * Builds a Docker image using a specified Dockerfile and build context.
 *
 * @param imageName  Full image name including tag, e.g. "amrthedev/jobportal:5"
 * @param dockerfile Absolute path to the Dockerfile inside the Jenkins workspace
 * @param context    Absolute path to the build context (source code root)
 */
def call(String imageName, String dockerfile, String context) {
    echo "Building Docker image: ${imageName}"
    echo "  Dockerfile : ${dockerfile}"
    echo "  Context    : ${context}"

    // docker.build() is provided by the Docker Pipeline plugin.
    // Second argument is passed directly to `docker build` as CLI flags.
    docker.build(imageName, "-f ${dockerfile} ${context}")
}
