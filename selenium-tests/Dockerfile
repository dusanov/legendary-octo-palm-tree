FROM gradle:7.6.0-jdk8 AS builder
WORKDIR /home/gradle/src
# build and cache dependencies
COPY --chown=gradle:gradle build.gradle settings.gradle /home/gradle/src/
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true
# build all
COPY --chown=gradle:gradle . /home/gradle/src
ENTRYPOINT ["gradle", "clean", "test"]
# ENTRYPOINT ["tail","-f","/dev/null"]
