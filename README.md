# XML Reader

This project was developed using Java 15 and Spring-Boot 2.4.1.  
This app analyses the content of big XML files. 

### To start the App:
1. Open terminal in the main catalog
2. Pull docker image from docker hub: `docker-compose pull`
3. Start app by: `docker-compose up -d`
4. App will work on https://localhost:8443.

### To stop service
1. Open a terminal and use this command:
2. `docker-compose down`  use `-v` if you want to remove volume.

### Try app
To analyze XML file you have to send a `POST` request to the endpoint `/analyze` with the request body 
having a URL address to an XML file (using f.e. curl or postman).

Example request: 

```
curl --location --request POST 'https://localhost:8443/analyze' \
--header 'Content-Type: application/json' \
--data-raw '{
  "url": "https://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml"
}'
```
This project uses HTTP/2 so SSL certificates are necessary. You can find them in this 
[**repository**](ssl-certs).

You can go to Swagger to see API docs:  
https://localhost:8443/swagger-ui/index.html#/reader-controller

### More info about the project

In this project, I used the newest versions of some tools like Java 15, Spring-Boot 2.4.1, JUnit 5, 
Docker-Compose 3.8, HATEOS, and more. The main target was to analyze bix XML files with app 
working 
with max 512MB of memory. To accomplish that I used Reactive HTTP/2 which was provided with Java11. Using that, app can parse big XML files line by line - it doesn't have to fetch the whole 
    document to the memory (which will be impossible with big files). To parse XML, app is using 
light tool [SimpleFramework](http://www.simpleframework.org/).  

App is integration and component tested using JUnit 5 and MockServer. 

This service is Dockerized. To simplify I used Spring-Boot BuildPacks to create Docker Image and 
because of that DockerFile is unnecessary. To create a new Docker Image and push it to [Docker Hub](https://hub.docker.com/r/jakubjakubowski/xmlreader) 
just start below script:  
```sh create_docker_image.sh```

To start app you can use `docker run` or using `docker-compose` which is more suitable. 

Please use an actuator to check the health of API endpoints:  
https://localhost:8443/actuator

Versions which I used:

* OpenJDK 15.0.1
* Docker 20.10.1
* Docker-compose 1.27.4

#### In case of any problems, please contact me.
