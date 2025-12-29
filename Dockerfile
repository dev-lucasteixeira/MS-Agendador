FROM gradle:9.2.1-jdk17-alpine AS BUILD
WORKDIR /app
COPY . .
RUN gradle build --no-daemon
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/agendador-de-tarefas.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/agendador-de-tarefas.jar"]