FROM gradle:8.1.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
RUN gradle clean build --no-daemon -i -x test -x javadoc

FROM openjdk:17-alpine
COPY --from=build /home/app/gaiax-catalogue-web/build/libs/gaiax-catalogue-web-0.0.1-SNAPSHOT.jar /home/app/gaia-x/app.jar
WORKDIR /home/app/gaia-x
EXPOSE 8189
ENTRYPOINT ["java", "-jar", "app.jar"]
