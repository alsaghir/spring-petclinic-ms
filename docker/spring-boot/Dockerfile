# Global Args
ARG WORKDIR_APP=/workspace/app
ARG EXTRACTED=${WORKDIR_APP}/target/extracted

# Build stage
FROM maven:3-eclipse-temurin-17-alpine AS builder

ARG SERVICE_NAME
ARG WORKDIR_APP
ARG EXTRACTED
WORKDIR ${WORKDIR_APP}

COPY pom.xml .
COPY ./docker/scripts ./scripts
COPY ${SERVICE_NAME}/*.xml ./${SERVICE_NAME}/
COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

RUN java scripts/Pom.java ${SERVICE_NAME}

RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve dependency:resolve-plugins dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 mvn -B install \
    && mkdir -p target/extracted  \
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
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
