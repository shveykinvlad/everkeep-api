FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=app/build/libs/app-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} everkeep.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "/everkeep.jar"]
