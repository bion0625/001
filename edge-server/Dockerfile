FROM maven:3.8.4 AS builder
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=builder /target/edge-server-0.0.1-SNAPSHOT.jar app.jar
ENV EUREKA_SERVER_URL=http://registry-server:8761/eureka/
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]