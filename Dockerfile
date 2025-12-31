FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# 캐시를 위해 먼저 gradle 관련만 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies

# 나머지 소스 복사 후 빌드
COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV TZ=Asia/Seoul
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
