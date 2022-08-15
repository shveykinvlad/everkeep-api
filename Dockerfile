# Stage 1: Compile and Build
# Set base image
FROM eclipse-temurin:17-jdk-jammy as build
# Set the working directory
WORKDIR /workspace/app

# Add the source code
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY app app

# Install all the dependencies
RUN chmod +x ./gradlew
RUN ./gradlew app:build -x test -x checkstyleMain -x checkstyleTest -x pmdMain -x pmdTest -x jacocoTestCoverageVerification -x jacocoTestReport -x spotbugsMain -x spotbugsTest
RUN mkdir -p app/build/libs/dependency && (cd app/build/libs/dependency; jar -xf ../*.jar)


# Stage 2: Serve app
# Set base image
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
# Copy the build output
ARG DEPENDENCY=/workspace/app/app/build/libs/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.everkeep.Application"]
