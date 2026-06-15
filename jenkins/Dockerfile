FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      docker.io \
      maven \
      curl \
      jq \
      git \
      ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

USER jenkins

RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
