# Spring Pet Clinic Micro Services

## Intro

This is distributed version of the Spring Pet Clinic Sample app built with Spring & Flutter Web. These microservices fork initially inspired by [`spring-petclinic-microservices`](https://github.com/spring-petclinic/spring-petclinic-microservices) to demonstrate how to split sample Spring application into microservices. Multiple technologies are considered for this demonstration which are

- Java 17 & Maven with 3rd party libraries (Lombok, Mapstruct, JOOQ, Flyway, Micrometer)
- Spring Boot (Web, Data JPA, Admin Server)
- Spring Cloud (Config, Eureka Discovery, Gateway)
- Docker
- Promtail
- Prometheus
- Loki
- Grafana
- Flutter Web
- H2 SQL Database

## Status & Progress

As this is still work in progress, there are multiple stages to be done for a complete demonstration.

- Implementation
  - Add custom labels and tags to prometheus and loki usage
  - Data initialization for vets and visits services
  - Complete front-end implementation
  - Include chaos (not final)
  - Unit tests
- Documentation and architecture showcase
  - Architecture doc
  - Commands for starting services locally using SDKs and docker desktop (i.e. in Intellij IDE)
  - Commands for starting services locally using docker desktop with docker being target platform
  - Commands for starting services locally using docker-compose
- All services to be up and running locally with default configuration
  - ~~Make sure JOOQ generation of classes done automatically~~ - done
  - Generate external config for Observability and tracing for plug & play
  - Validate functionality of Grafana
  - Custom Labels in Grafana for prometheus and loki
- ~~Create separate module for testing all the up and running service automating that everything is up and running as expected~~ - donw
- All services to be up and running locally
- All services to be up and running using docker with and without local SDK
- All services to be up and running using docker compose

## Start services locally

### Prerequisites

- JDK 17
- Maven 3.9.x
- Flutter SDK >= 3.10.5
- Docker >= 24.0.2

### Start services locally

- Run the following to start database and observability services

```sh
# Create network for our usage
docker network create -d bridge MyBridgeNetwork

# Run H2 Database
docker container run -d --name h2-server --network=MyBridgeNetwork -d -p 9092:1521 -p 81:81 --mount 'type=volume,src=h2-data,dst=/opt/h2-data' -e H2_OPTIONS=-ifNotExists oscarfonts/h2

# Run tracing and monitoring servers
docker container run -d --name zipkin-server --network MyBridgeNetwork -p 9411:9411 openzipkin/zipkin
docker container run -d --name promtail-server --network MyBridgeNetwork -v ${pwd}/docker/promtail/promtail-config.yaml:/mnt/config/promtail-config.yaml -v ${pwd}/spring-petclinic-api-gateway/log:/var/log/spring-petclinic-api-gateway -v ${pwd}/spring-petclinic-customer-service/log:/var/log/spring-petclinic-customer-service grafana/promtail:2.8.0 --config.file=/mnt/config/promtail-config.yaml
docker container run -d --name loki-server --network MyBridgeNetwork -v ${pwd}/docker/loki/loki-config.yaml:/mnt/config/loki-config.yaml -p 3100:3100 grafana/loki:2.8.0 --config.file=/mnt/config/loki-config.yaml
docker container run -d --name prometheus-server --network MyBridgeNetwork -p 9090:9090 -v ${pwd}/docker/prometheus/:/etc/prometheus/ prom/prometheus
docker container run -d --name=grafana-server --network MyBridgeNetwork -p 3000:3000 -v ${pwd}/docker/grafana/provisioning:/etc/grafana/provisioning -v ${pwd}/docker/grafana/grafana.ini:/etc/grafana/grafana.ini -v ${pwd}/docker/grafana/dashboards:/var/lib/grafana/dashboards grafana/grafana-oss

# Run Spring boot apps
mvn -pl spring-petclinic-config-server clean spring-boot:run -"Dspring-boot.run.profiles=native" -"Dspring-boot.run.arguments=--config.file-repo=D:/Code/spring-petclinic-ms-config/"
mvn -pl spring-petclinic-discovery-server clean spring-boot:run
mvn -pl spring-petclinic-admin-server clean spring-boot:run
mvn -pl spring-petclinic-api-gateway clean spring-boot:run
mvn -pl spring-petclinic-customer-service clean spring-boot:run -"Dspring-boot.run.profiles=default,h2"

# Validate everything is working correctly bu running
# the test service and open spring-petclinic-test-service/target/site/index.html
# in a browser
mvn -pl spring-petclinic-test-service clean verify site -Dmaven.plugin.validation=VERBOSE
```

- Services locations
  - Discovery Server - http://localhost:7772
  - Config Server - http://localhost:7771
  - Flutter Web UI & API Gateway - http://localhost:7778
  - Customers - http://localhost:7773
  - Vets - http://localhost:7774
  - Visits - http://localhost:7775
  - Tracing Server (Zipkin) - http://localhost:9411/zipkin/ (we use openzipkin)
  - Admin Server (Spring Boot Admin) - http://localhost:7776
  - Grafana Dashboards - http://localhost:3000
  - Prometheus - http://localhost:9090
  - Loki - http://localhost:3100/metrics

## Start services using docker

### Prerequisites

- Docker >= 24.0.2