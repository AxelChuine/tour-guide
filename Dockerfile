FROM openjdk:17-alpine3.14

COPY target/

ENTRYPOINT ["java", "-jar", ""]