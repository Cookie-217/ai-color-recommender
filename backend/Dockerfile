# ---- 构建阶段 ----
FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -e dependency:resolve || true
COPY src ./src
RUN mvn -B -e clean package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/ai-color-recommender.jar app.jar
EXPOSE 8080
# 生产部署请通过环境变量覆盖：APP_JWT_SECRET、DB_URL、DB_USERNAME、DB_PASSWORD、APP_CORS_ORIGINS
ENTRYPOINT ["java", "-jar", "app.jar"]
