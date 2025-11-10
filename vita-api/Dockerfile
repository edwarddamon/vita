FROM openjdk:8

MAINTAINER Edward

ENV ACTIVE_PROFILE="test"
ENV PRO_NAME="vita"
ENV SERVICE_NAME = "vita-api"
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/${PRO_NAME}/dumps/${SERVICE_NAME}-heap-dump.hprof -Xlog:gc*:file=/opt/${PRO_NAME}/logs/${SERVICE_NAME}-gc.log:time:filecount=10,filesize=10M"

# 设置字符集编码
ENV LC_ALL=zh_CN.utf8
ENV LANG=zh_CN.utf8
ENV LANGUAGE=zh_CN.utf8
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8

# 镜像时区设置为东八区
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

# 创建一个目录存放jar包
RUN mkdir -p /opt/${PRO_NAME}

# 设置开放端口号
EXPOSE 8023

# 添加jar包，存放路径以及重命名
ADD target/*.jar /opt/${PRO_NAME}/${SERVICE_NAME}.jar

# 添加进入docker容器后的目录
WORKDIR /opt/${PRO_NAME}

ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar -Dspring.profiles.active=${ACTIVE_PROFILE} /opt/${PRO_NAME}/${SERVICE_NAME}.jar"]

# 启动命令
# docker run -d -p 8023:8023 -e "JAVA_OPTS=-Xms125m -Xmx512m" -e "ACTIVE_PROFILE=dev" -v /opt/vita/logs:/opt/vita/logs/ --name vita-api 镜像ID