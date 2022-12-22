FROM gradle:7.6.0-jdk8 AS builder
WORKDIR /home/gradle/src
# build and cache dependencies
COPY --chown=gradle:gradle build.gradle settings.gradle /home/gradle/src/
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true
# build all
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle bootJar --no-daemon
RUN java -Djarmode=layertools -jar build/libs/SFR-0.0.1-SNAPSHOT.jar extract

FROM openjdk:8-jre-slim
EXPOSE 8080
WORKDIR /home/gradle/src
COPY --from=builder /home/gradle/src/dependencies/ ./
COPY --from=builder /home/gradle/src/snapshot-dependencies/ ./
COPY --from=builder /home/gradle/src/spring-boot-loader/ ./
COPY --from=builder /home/gradle/src/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
