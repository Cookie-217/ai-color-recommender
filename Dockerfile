# ---- 1. 前端构建 ----
FROM node:20-alpine AS frontend
WORKDIR /fe
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- 2. 后端构建（打包前端静态资源进 jar） ----
FROM maven:3.9.9-eclipse-temurin-8 AS backend
WORKDIR /app
COPY backend/pom.xml .
RUN mvn -B dependency:resolve || true
COPY backend/src ./src
COPY --from=frontend /fe/dist ./src/main/resources/static
RUN mvn -B -e clean package -DskipTests

# ---- 3. 运行 ----
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=backend /app/target/ai-color-recommender.jar app.jar
EXPOSE 8080
# 生产部署请通过环境变量覆盖：APP_JWT_SECRET、DB_URL、DB_USERNAME、DB_PASSWORD、APP_CORS_ORIGINS
ENTRYPOINT ["java", "-jar", "app.jar"]
