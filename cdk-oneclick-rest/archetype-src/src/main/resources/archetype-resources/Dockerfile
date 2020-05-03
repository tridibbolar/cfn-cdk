FROM openjdk:8u131-jre-alpine

MAINTAINER tcgdigital

EXPOSE 8080

WORKDIR /tmp

COPY /target/${artifactId}-1.0.jar /tmp

CMD ["java","-jar","${artifactId}-1.0.jar"]


