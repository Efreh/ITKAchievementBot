# Используем легковесный образ с Java 17 (Alpine)
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный jar-файл
COPY target/JavaCodeBot-1.0.0-SNAPSHOT.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/app.jar"]