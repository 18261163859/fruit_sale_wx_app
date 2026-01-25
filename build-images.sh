#!/bin/bash

# ===========================================
# 水果销售平台 镜像打包脚本（精简版）
# 只打包前端和后端镜像
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="${SCRIPT_DIR}/docker-images"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ARCHIVE_NAME="fruit-images-${TIMESTAMP}.tar.gz"

# 镜像名称
BACKEND_TAG="fruit-sale/backend:latest"
ADMIN_WEB_TAG="fruit-sale/admin-web:latest"

# 日志函数
log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检查 Docker
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未运行"
        exit 1
    fi
}

# 构建后端
build_backend() {
    log_info "构建后端镜像..."
    cd "${SCRIPT_DIR}/backend"
    docker build --platform linux/amd64 --no-cache -t "$BACKEND_TAG" -t "fruit-sale/backend:${TIMESTAMP}" .
    log_success "后端镜像构建完成"
}

# 构建前端
build_admin_web() {
    log_info "构建管理后台镜像..."
    cd "${SCRIPT_DIR}/admin_web"
    docker build --platform linux/amd64 --no-cache -t "$ADMIN_WEB_TAG" -t "fruit-sale/admin-web:${TIMESTAMP}" .
    log_success "管理后台镜像构建完成"
}

# 导出镜像
export_images() {
    log_info "导出镜像..."
    mkdir -p "$OUTPUT_DIR"
    
    docker save "$BACKEND_TAG" "$ADMIN_WEB_TAG" \
        -o "${OUTPUT_DIR}/fruit-images.tar"
    
    log_success "镜像已导出到 ${OUTPUT_DIR}/fruit-images.tar"
}

# 创建压缩包
create_archive() {
    cd "$OUTPUT_DIR"
    # 只打包 .tar 文件和 import-images.sh
    tar -czvf "${SCRIPT_DIR}/${ARCHIVE_NAME}" fruit-images.tar import-images.sh
    SIZE=$(du -h "${SCRIPT_DIR}/${ARCHIVE_NAME}" | cut -f1)
    log_success "压缩包: ${ARCHIVE_NAME} (${SIZE})"
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help       显示帮助"
    echo "  --build-only     仅构建镜像，不导出"
    echo "  --export-only    仅导出已有镜像"
    echo ""
}

# 主函数
main() {
    local build_only=false
    local export_only=false

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help) show_help; exit 0 ;;
            --build-only) build_only=true; shift ;;
            --export-only) export_only=true; shift ;;
            *) log_error "未知参数: $1"; exit 1 ;;
        esac
    done

    echo "=========================================="
    echo "  水果销售平台 镜像打包工具"
    echo "=========================================="
    echo ""

    check_docker

    if [ "$export_only" = true ]; then
        export_images
        create_archive
        exit 0
    fi

    if [ "$build_only" = true ]; then
        build_backend
        build_admin_web
        exit 0
    fi

    # 构建并导出
    build_backend &
    PID_BACKEND=$!
    build_admin_web &
    PID_FRONTEND=$!

    wait $PID_BACKEND || { log_error "后端构建失败"; exit 1; }
    wait $PID_FRONTEND || { log_error "前端构建失败"; exit 1; }

    export_images
    create_archive

    echo ""
    echo "=========================================="
    echo "  完成!"
    echo "=========================================="
    echo ""
    echo "上传到服务器并执行:"
    echo "  tar -xzf $ARCHIVE_NAME"
    echo "  cd docker-images"
    echo "  ./import-images.sh"
    echo ""
}

main "$@"