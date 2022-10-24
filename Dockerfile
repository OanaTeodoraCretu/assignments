FROM maven:3.8-openjdk-17
COPY schedules /schedules
WORKDIR /schedules
RUN mvn clean install
CMD ["/bin/sh","-c","exec java -jar -Dspring.profiles.active=local ~/.m2/repository/com/geosatis/schedules/0.0.1-SNAPSHOT/schedules-0.0.1-SNAPSHOT.jar"]
