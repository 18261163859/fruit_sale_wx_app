#!/bin/bash

# ===========================================
# 水果销售平台 Docker 镜像导入脚本
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未运行，请先启动 Docker"
        exit 1
    fi
    log_success "Docker 运行正常"
}

# 导入镜像
import_images() {
    log_info "开始导入镜像..."

    for tar_file in "${SCRIPT_DIR}"/*.tar; do
        if [ -f "$tar_file" ]; then
            filename=$(basename "$tar_file")
            log_info "导入 ${filename}..."
            docker load -i "$tar_file"
            log_success "${filename} 导入完成"
        fi
    done

    log_success "所有镜像导入完成"
}

# 显示导入结果
show_result() {
    echo ""
    echo "=========================================="
    echo -e "${GREEN}导入完成！${NC}"
    echo "=========================================="
    echo ""
    echo "已导入的镜像:"
    docker images | grep -E "(fruit-|mysql|redis|minio)" | head -10
    echo ""
    echo "下一步操作:"
    echo "  1. 启动所有服务: docker-compose up -d"
    echo "  2. 查看服务状态: docker-compose ps"
    echo "  3. 查看日志: docker-compose logs -f"
    echo ""
    echo "访问地址:"
    echo "  - Admin Web: http://localhost"
    echo "  - Backend API: http://localhost:8000/api"
    echo "  - API 文档: http://localhost:8000/api/doc.html"
    echo "  - MinIO 控制台: http://localhost:9001"
    echo ""
}

# 主函数
main() {
    echo ""
    echo "=========================================="
    echo "  水果销售平台 Docker 镜像导入工具"
    echo "=========================================="
    echo ""

    check_docker
    import_images
    show_result
}

main "$@"
