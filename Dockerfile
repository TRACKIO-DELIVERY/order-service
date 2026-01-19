# -------- Build stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY ./order-core/target/order-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
