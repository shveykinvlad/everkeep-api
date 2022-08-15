FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY app app

RUN chmod +x ./gradlew
RUN ./gradlew app:build -x test -x checkstyleMain -x checkstyleTest -x pmdMain -x pmdTest -x jacocoTestCoverageVerification -x jacocoTestReport -x spotbugsMain -x spotbugsTest
RUN mkdir -p app/build/libs/dependency && (cd app/build/libs/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/app/build/libs/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.everkeep.Application"]
