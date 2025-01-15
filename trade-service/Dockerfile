FROM maven:3.8.4 AS builder
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=builder /target/trade-service-0.0.1-SNAPSHOT.jar app.jar
ENV ACTIVE=prd
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=${ACTIVE}", "-jar", "app.jar"]