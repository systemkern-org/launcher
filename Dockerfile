FROM postgres:9.6.3-alpine
ADD default/target/default-0.1.jar /var
EXPOSE 8085
ENTRYPOINT ["java","-jar","default-0.1.jar"]