# Сборка клиентского модуля
FROM openjdk:11 AS client-builder
LABEL authors="BDORZHO"
ADD /client/target/stats-client-0.0.1-SNAPSHOT.jar /app/stats-client.jar

# Сборка модуля dto
FROM openjdk:11 AS dto-builder
LABEL authors="BDORZHO"
ADD /dto/target/common-dto-0.0.1-SNAPSHOT.jar /app/common-dto.jar

# Сборка модуля статистики
FROM openjdk:11 AS service-builder
LABEL authors="BDORZHO"
ADD service/target/stats-service-0.0.1-SNAPSHOT.jar /app/stats-service.jar

# Финальный контейнер
FROM openjdk:11
WORKDIR /app
COPY --from=client-builder /app/stats-client.jar /app/
COPY --from=dto-builder /app/common-dto.jar /app/
COPY --from=service-builder /app/stats-service.jar /app/

CMD ["java", "-jar", "stats-client.jar"]
CMD ["java", "-jar", "common-dto.jar"]
CMD ["java", "-jar", "stats-service.jar"]