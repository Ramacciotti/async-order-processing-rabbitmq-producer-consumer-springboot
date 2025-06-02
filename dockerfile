FROM eclipse-temurin:17-jdk-alpine

# Instala bash porque Alpine não vem com bash por padrão
RUN apk add --no-cache bash

WORKDIR /app

# Copia o jar da aplicação
COPY target/async-0.0.2-SNAPSHOT.jar app.jar

# Copia o script wait-for-it e dá permissão de execução
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Roda o wait-for-it para esperar o RabbitMQ subir e depois inicia a aplicação
ENTRYPOINT ["/wait-for-it.sh", "rabbitmq", "5672", "--", "java", "-jar", "app.jar"]
