FROM swr.cn-east-3.myhuaweicloud.com/upuphone-cloudplatform/maven-upuphone:3.8.3 as builder
COPY . /build/
WORKDIR /build
RUN mvn clean -U -f ./pom.xml clean package -Dmaven.test.skip=true


FROM openjdk:1.8
MAINTAINER Casent
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
COPY --from=builder /build/usercenter-start/target/usercenter-start.jar  /app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -jar -Dlog.path=logs/${POD_NAME} /app.jar