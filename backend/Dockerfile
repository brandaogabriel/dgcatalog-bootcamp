FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./target/dgcatalog-0.0.1-SNAPSHOT.jar dgcatalog.jar
ENTRYPOINT ["java","-jar","/dgcatalog.jar"]