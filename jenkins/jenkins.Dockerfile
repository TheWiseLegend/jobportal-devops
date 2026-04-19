# Base image — official Jenkins LTS on Debian 13 (trixie)
FROM jenkins/jenkins:lts

# Switch to root to install system packages
USER root

# Step 1: Install prerequisites needed to add Docker's apt repository over HTTPS
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Step 2: Add Docker's official GPG key
# This proves the packages we download are genuinely from Docker, not tampered with
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg \
    -o /etc/apt/keyrings/docker.asc && \
    chmod a+r /etc/apt/keyrings/docker.asc

# Step 3: Add Docker's apt repository pointing at trixie
RUN echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
    https://download.docker.com/linux/debian trixie stable" \
    > /etc/apt/sources.list.d/docker.list

# Step 4: Install only the Docker CLI — don't need the daemon (it lives on the host)
RUN apt-get update && apt-get install -y \
    docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*

# Create docker group with GID 1001 to match the host, and add jenkins user to it
RUN groupadd -f -g 1001 docker && usermod -aG docker jenkins

# Switch back to jenkins user — never run Jenkins as root
USER jenkins

