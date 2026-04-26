FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the pom first to leverage Docker cache for dependencies
COPY microservico-notif/pom.xml ./
COPY microservico-notif/src ./src

# Build the application (skip tests for faster builds; remove -DskipTests for CI that runs tests)
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /workspace/target/*.jar ./app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms128m -Xmx512m"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
