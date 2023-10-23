FROM debian:latest

ARG NVM_VERSION=0.39.5
ARG JDK_VERSION=17.0.9
ENV NODE_VERSION=18.18.2
ARG DEBIAN_FRONTEND=noninteractive
ARG TZ=America/Los_Angeles
ARG DOCKER_IMAGE_NAME_TEMPLATE="mcr.microsoft.com/playwright:v%version%-jammy"

RUN apt-get update -qq  \
    && apt-get install -qq -y --no-install-recommends apt-transport-https gnupg2 wget ca-certificates \
    && apt-get clean

RUN apt-get update -qq && apt-get install -qq -y --no-install-recommends \
    zip \
    unzip \
    curl \
    wget \
    gpg \
    ca-certificates \
    git \
    openssh-client \
    && apt-get clean


RUN apt-get update -qq && apt-get install -qq -y --no-install-recommends \
    libglib2.0-0 \                                                               
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libdbus-1-3 \
    libxcb1 \
    libxkbcommon0 \
    libatspi2.0-0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    && apt-get clean

RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v${NVM_VERSION}/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="$NVM_DIR/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN node --version
RUN npm --version
RUN npm install -g yarn

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENV HOME="/root"
# Environment variables needed for SDKs and builders
ENV SDKMAN_DIR="$HOME/.sdkman"
ENV JAVA_HOME="$SDKMAN_DIR/candidates/java/current"
ENV MAVEN_HOME="$SDKMAN_DIR/candidates/maven/current"
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV PATH="$MAVEN_HOME/bin:$PATH"

RUN curl -s "https://get.sdkman.io" | bash \
    && chmod a+x "/root/.sdkman/bin/sdkman-init.sh" \
    && source "/root/.sdkman/bin/sdkman-init.sh" \
    && sdk install java ${JDK_VERSION}-tem \
    && sdk install maven

WORKDIR /opt/app
ARG SERVICE_NAME=spring-petclinic-test-service
COPY pom.xml .
COPY ./docker/scripts ./scripts

COPY ${SERVICE_NAME}/*.xml ./${SERVICE_NAME}/
COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

RUN java --version
RUN mvn --version

RUN java scripts/Pom.java ${SERVICE_NAME}

COPY ./docker/playwright/start.sh ./start.sh
RUN chmod a+x ./start.sh

RUN java --version
RUN mvn --version
ENV DEBUG=pw:*
ENV NODE_TLS_REJECT_UNAUTHORIZED=0
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

CMD ["./start.sh"]
