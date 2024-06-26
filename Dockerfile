FROM azul/zulu-openjdk-alpine:21
ENV DATASOURCE_URL DATASOURCE_URL
ENV DATASOURCE_USERNAME DATASOURCE_USERNAME
ENV DATASOURCE_PASSWORD DATASOURCE_PASSWORD
ENV AWS_ENDPOINT AWS_ENDPOINT
ENV AWS_SQS_ENDPOINT_ENVIA_EMAILS AWS_SQS_ENDPOINT_ENVIA_EMAILS
ENV AWS_CLIENT_ID AWS_CLIENT_ID
ENV AWS_CLIENT_SECRET AWS_CLIENTE_SECRET
ENV AWS_REGION AWS_REGION
EXPOSE 8082
COPY target/*.jar clientes-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/clientes-0.0.1-SNAPSHOT.jar"]
