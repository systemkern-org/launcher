FROM droptica/maildev:latest
ADD runtime-cli/target/runtime-cli-0.1.jar /var
EXPOSE 8085
ENTRYPOINT ["java","-jar","runtime-cli-0.1.jar"]