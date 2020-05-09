FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD everkeep-impl/build/libs/everkeep-impl-1.0-SNAPSHOT.jar everkeep.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "/everkeep.jar"]