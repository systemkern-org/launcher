FROM postgres:9.6.3-alpine
ADD runtime-cli/target/runtime-cli-0.1.jar /var
EXPOSE 8085
ENTRYPOINT ["java","-jar","runtime-cli-0.1.jar"]