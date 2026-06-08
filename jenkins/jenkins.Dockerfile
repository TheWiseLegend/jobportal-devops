# Base image — official Jenkins LTS on Debian 13 (trixie)
FROM jenkins/jenkins:lts

USER root

# Step 1: Install prerequisites for apt repositories
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    && rm -rf /var/lib/apt/lists/*

# Step 2: Add Docker's official GPG key
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg \
    -o /etc/apt/keyrings/docker.asc && \
    chmod a+r /etc/apt/keyrings/docker.asc

# Step 3: Add Docker's apt repository
RUN echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
    https://download.docker.com/linux/debian trixie stable" \
    > /etc/apt/sources.list.d/docker.list

# Step 4: Install Docker CLI
RUN apt-get update && apt-get install -y \
    docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*

# Step 5: Install Maven
RUN apt-get update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Step 6: Add Eclipse Temurin (Adoptium) repository and install Java 25
# The container ships with Java 21. The app's pom.xml requires Java 25.
# Java 21 cannot compile Java 25 source code — hence this step.
RUN curl -fsSL https://packages.adoptium.net/artifactory/api/gpg/key/public \
    | gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] \
    https://packages.adoptium.net/artifactory/deb \
    $(awk -F= '/^VERSION_CODENAME/{print $2}' /etc/os-release) main" \
    > /etc/apt/sources.list.d/adoptium.list && \
    apt-get update && \
    apt-get install -y temurin-25-jdk && \
    rm -rf /var/lib/apt/lists/*

# Set Java 25 as the active JDK so Maven uses it for compilation
ENV JAVA_HOME=/usr/lib/jvm/temurin-25-jdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Step 7: Match Docker group GID to host
RUN groupadd -f -g 1001 docker && usermod -aG docker jenkins

USER jenkins
