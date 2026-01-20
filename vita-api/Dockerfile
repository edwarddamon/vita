# 基础镜像：阿里云龙蜥JDK17精简版
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6

MAINTAINER Edward

ENV PRO_NAME="vita"
ENV SERVICE_NAME="vita-api"
# 移除硬编码的ACTIVE_PROFILE，仅保留空默认值（运行时覆盖）
ENV ACTIVE_PROFILE=""
# JDK17通用JVM参数（无环境差异）
ENV JAVA_OPTS="\
-Xms512m -Xmx512m \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/opt/${PRO_NAME}/dumps/${SERVICE_NAME}/heap-dump.hprof \
-Xlog:gc*:file=/opt/${PRO_NAME}/logs/${SERVICE_NAME}/gc.log:time,level,tags:filecount=10,filesize=10M \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+ParallelRefProcEnabled \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseZGC \
-Dfile.encoding=UTF-8 \
-Dsun.jnu.encoding=UTF-8 \
-Dnacos.logging.path=/opt/${PRO_NAME}/logs/nacos \
-Dnacos.logs.path=/opt/${PRO_NAME}/logs/nacos \"

# ========== 字符集 & 时区配置（通用） ==========
RUN set -eux && \
    yum install -y glibc-langpack-zh tzdata curl && \
    yum clean all && \
    rm -rf /var/cache/yum/*

ENV LC_ALL=zh_CN.UTF-8
ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN.UTF-8

RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# ========== 目录 & 权限配置（通用） ==========
RUN mkdir -p /opt/${PRO_NAME}/{logs,dumps} && \
    groupadd -r vita && useradd -r -g vita vita && \
    chown -R vita:vita /opt/${PRO_NAME}

EXPOSE 8023
WORKDIR /opt/${PRO_NAME}

# ========== 复制JAR包（通用，无环境差异） ==========
ADD target/*.jar /opt/${PRO_NAME}/${SERVICE_NAME}.jar
RUN chown vita:vita /opt/${PRO_NAME}/${SERVICE_NAME}.jar && \
    chmod 755 /opt/${PRO_NAME}/${SERVICE_NAME}.jar

USER vita

# ========== 启动命令（通用，依赖运行时环境变量） ==========
# 若ACTIVE_PROFILE未指定，默认使用dev（可选，增加容错）
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dspring.profiles.active=${ACTIVE_PROFILE:-dev} /opt/${PRO_NAME}/${SERVICE_NAME}.jar"]