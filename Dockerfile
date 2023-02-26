FROM eclipse-temurin:17.0.6_10-jdk-jammy

COPY build/libs/expenses-1.0.0.jar expenses-1.0.0.jar

RUN mkdir -p log

ARG TARGETARCH
RUN echo "The arch is: $TARGETARCH"

ENTRYPOINT ["java", "-jar", "-Xms4096m", "-Xmx8192m", "/expenses-1.0.0.jar"]