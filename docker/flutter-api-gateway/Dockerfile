# Global Args
ARG JDK_VERSION=17.0.7
ARG WORKDIR_APP=/workspace/app
ARG EXTRACTED=${WORKDIR_APP}/target/extracted

#Stage 1 - Install dependencies and build the flutter app
FROM debian:latest AS builder

# Use bash instead of sh
SHELL ["/bin/bash", "-c"]

# Local args and Global Args (must redeclare for usage)
ARG DEBIAN_FRONTEND=noninteractive

ARG JDK_VERSION
ARG SERVICE_NAME
ARG WORKDIR_APP
ARG FLUTTER_PROJECT_PPATH=${SERVICE_NAME}/src/main/dart
ARG EXTRACTED
WORKDIR ${WORKDIR_APP}

ENV HOME="/root"

# Environment variables needed for SDKs and builders
ENV SDKMAN_DIR="$HOME/.sdkman"
ENV JAVA_HOME="$SDKMAN_DIR/candidates/java/current"
ENV MAVEN_HOME="$SDKMAN_DIR/candidates/maven/current"
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV PATH="$MAVEN_HOME/bin:$PATH"
# Flutter
ENV PATH="/usr/local/flutter/bin:/usr/local/flutter/bin/cache/dart-sdk/bin:${PATH}"

# For certificate validation pass
RUN apt-get update -qq  \
    && apt-get install -qq -y --no-install-recommends apt-transport-https gnupg2 wget ca-certificates \
    && apt-get clean

RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo 'deb [arch=amd64] https://storage.googleapis.com/download.dartlang.org/linux/debian stable main' | tee /etc/apt/sources.list.d/dart_stable.list \
   # For Flutter Web SDK
    && echo 'deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main' | tee /etc/apt/sources.list.d/google-chrome.list

# Install flutter dependencies
RUN apt-get update -qq && apt-get install -qq -y --no-install-recommends \
    libglu1-mesa \
    clang  \
    cmake  \
    ninja-build  \
    pkg-config  \
    libgtk-3-dev  \
    liblzma-dev \
    curl\
    git \
    unzip \
    zip \
    google-chrome-stable  \
    && apt-get clean

# Clone the flutter repo
RUN --mount=type=cache,target=/usr/local/flutter git -C /usr/local/flutter pull || git clone https://github.com/flutter/flutter.git /usr/local/flutter \
    && ls -lah /usr/local/ \
    && flutter config --no-analytics --enable-web --no-enable-android --no-enable-ios \
    && flutter doctor -v \
    && flutter channel master  \
    # Enable flutter web
    && flutter pub global activate webdev \
    # Cache Web SDK
    && flutter precache --web

# Install JDK & Maven
RUN curl -s "https://get.sdkman.io" | bash \
    && chmod a+x "/root/.sdkman/bin/sdkman-init.sh" \
    && source "/root/.sdkman/bin/sdkman-init.sh" \
    && sdk install java ${JDK_VERSION}-tem \
    && sdk install maven

COPY pom.xml .
COPY ./docker/scripts ./scripts
COPY ${SERVICE_NAME}/*.xml ./${SERVICE_NAME}/
COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

RUN java scripts/Pom.java ${SERVICE_NAME}

RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve dependency:resolve-plugins dependency:go-offline

RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=cache,target=/usr/local/flutter \
    mvn -B install \
    && mkdir -p target/extracted \
    && java -Djarmode=layertools -jar ${SERVICE_NAME}/target/*.jar extract --destination target/extracted

# Run stage using non-root user
# https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#container-images.dockerfiles
FROM eclipse-temurin:17-jdk-alpine
ARG USERNAME=developer
ARG GROUPNAME=developergroup
RUN addgroup --gid 10001 -S "${GROUPNAME}"; adduser -u 10000 --ingroup "${GROUPNAME}" --disabled-password -S "${USERNAME}"

RUN apk --no-cache add curl

# Tell docker that all future commands should run as the user
USER $USERNAME

ARG WORKDIR_APP
ARG EXTRACTED
WORKDIR $WORKDIR_APP

# Spring Boot build files
COPY --from=builder --chown=${USERNAME}:${GROUPNAME} $EXTRACTED/dependencies/ ./
COPY --from=builder --chown=${USERNAME}:${GROUPNAME} $EXTRACTED/spring-boot-loader/ ./
COPY --from=builder --chown=${USERNAME}:${GROUPNAME} $EXTRACTED/snapshot-dependencies/ ./
COPY --from=builder --chown=${USERNAME}:${GROUPNAME} $EXTRACTED/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]