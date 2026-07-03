FROM openjdk:17

WORKDIR /app

COPY . .

RUN ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/*.jar"]