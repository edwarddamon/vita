#!/bin/bash
set -euo pipefail

# 必须在项目根目录下执行。
# 通过环境变量配置镜像仓库，避免在代码中硬编码私有地址：
#   export DOCKER_REGISTRY=registry.example.com
#   export DOCKER_NAMESPACE=vita
modules=(
  "vita-gateway"
  "vita-auth-center"
  "vita-api"
  "vita-message"
  "vita-im"
)

remoteRepository="${DOCKER_REGISTRY:-registry.example.com}"
namespace="${DOCKER_NAMESPACE:-vita}"
tagSuffix="$(date +%Y%m%d_%H%M)"

echoPrefix="\033[32m"
echoSuffix="\033[0m"

for projectName in "${modules[@]}"
do
  imageName="${projectName}:${tagSuffix}"
  docker build -t "${imageName}" "./${projectName}"
  echo -e "${echoPrefix} [docker-deploy] >>>>> [${imageName}] 本地镜像构建成功! ${echoSuffix}"

  remoteImage="${remoteRepository}/${namespace}/${projectName}:${tagSuffix}"
  docker tag "${imageName}" "${remoteImage}"
  docker push "${remoteImage}"
  echo -e "${echoPrefix} [docker-deploy] >>>>> [${imageName}] 镜像推送到 [${remoteImage}] 成功! ${echoSuffix}"

  docker rmi "${imageName}"
  docker rmi "${remoteImage}"
  echo -e "${echoPrefix}  [docker-deploy] >>>>> [${imageName}] 和 [${remoteImage}] 镜像删除成功! ${echoSuffix}"
done

echo -e "${echoPrefix}  [docker-deploy] >>>>> 所有服务推送成功! ${echoSuffix}"
