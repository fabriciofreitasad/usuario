FROM gradle:7.5-jdk17 As build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/usuario.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/usuario.jar"]