#!/bin/bash

echo "========================================="
echo "  高端云南水果销售系统 - 启动脚本"
echo "========================================="

# 检查 Java 版本
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$java_version" -lt 17 ]; then
    echo "错误: 需要 Java 17 或更高版本"
    exit 1
fi

echo "✓ Java 版本检查通过"

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到 Maven，请先安装 Maven"
    exit 1
fi

echo "✓ Maven 检查通过"

# 清理并编译
echo ""
echo "正在清理并编译项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo "✓ 编译成功"

# 启动应用
echo ""
echo "正在启动应用..."
java -jar target/fruit-sale-backend.jar

exit 0
