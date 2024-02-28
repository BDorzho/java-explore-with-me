FROM openjdk:11
LABEL authors="BDORZHO"
ADD target/stats-service-0.0.1-SNAPSHOT.jar stats-service.jar

ENTRYPOINT ["java", "-jar", "stats-service.jar"]