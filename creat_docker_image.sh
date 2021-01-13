#!/bin/sh

TAG="1.0.0"
DOCKER_USER=user
DOCKER_PASSWORD=changeme
./mvnw spring-boot:build-image
docker image tag xmlreader:${TAG} jakubjakubowski/xmlreader:${TAG}
docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}
docker image push jakubjakubowski/xmlreader:${TAG}
docker logout
