FROM openjdk:17-jdk-alpine

# 필요한 패키지 설치
RUN apk add --no-cache tzdata

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG JAR_PATH=build/libs/*.jar
COPY ${JAR_PATH} app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
