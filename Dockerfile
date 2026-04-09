# ==========================================
# Stage 1: Build the Modern Vite Frontend
# ==========================================
FROM node:20 AS frontend-build
WORKDIR /app

# Copy the frontend files
COPY frontend/ ./frontend/

# Create the output directory where Vite expects to place the built files
RUN mkdir -p /app/src/main/resources/static

# Install NPM dependencies and build
WORKDIR /app/frontend
RUN npm install
RUN npm run build

# ==========================================
# Stage 2: Build the Spring Boot Backend
# ==========================================
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app

# Copy Maven wrappers and POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permissions to the maven wrapper
RUN chmod +x ./mvnw

# Resolve dependencies (cached layer)
RUN ./mvnw dependency:go-offline || true

# Copy the Java source code
COPY src ./src

# Explicitly copy the finished HTML/JS/CSS from Stage 1 into the Java static folder!
COPY --from=frontend-build /app/src/main/resources/static ./src/main/resources/static

# Package the application into a single executable .jar file
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 3: Minimal Production Image
# ==========================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy ONLY the final compiled .jar file from Stage 2 (makes deployment super lightweight)
COPY --from=backend-build /app/target/*.jar app.jar

# Render needs to know our app listens on 8080
EXPOSE 8080

# Boot it up!
ENTRYPOINT ["java", "-jar", "app.jar"]
