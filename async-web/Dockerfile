FROM registry.cn-chengdu.aliyuncs.com/yundouhealth/base:ubuntu16.04-openjdk:17-jdk
ENV TZ=Asia/Shanghai
ADD target/async-web.jar /async-web.jar
ADD start.sh /start.sh
RUN chmod 777 /start.sh
ARG APPNAME
ENV APP_NAME=${APPNAME}
ENV SERVICE_NAME=192.168.7.11
CMD [ "top" ]
ENTRYPOINT exec java ${JAVA_OPTS} -jar /async-web.jar
EXPOSE 20703