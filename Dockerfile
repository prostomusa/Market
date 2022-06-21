FROM openjdk:11
ADD target/market.jar /app/market.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "market.jar"]