#!/bin/bash

# ===========================================
# 水果销售平台 Docker 镜像打包脚本
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="${SCRIPT_DIR}/docker-images"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ARCHIVE_NAME="fruit-sale-images-${TIMESTAMP}.tar.gz"

# 镜像名称
BACKEND_IMAGE="fruit-backend:latest"
ADMIN_WEB_IMAGE="fruit-admin-web:latest"

# 基础镜像（用于构建）
BASE_IMAGES=(
    "maven:3.9-eclipse-temurin-17"
    "eclipse-temurin:17-jre"
    "node:20-alpine"
    "nginx:alpine"
)

# 最大重试次数
MAX_RETRIES=3
# 重试间隔（秒）
RETRY_DELAY=5

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未运行，请先启动 Docker"
        exit 1
    fi
    log_success "Docker 运行正常"
}

# 带重试的镜像拉取
pull_with_retry() {
    local image=$1
    local retries=0

    while [ $retries -lt $MAX_RETRIES ]; do
        log_info "拉取 $image (尝试 $((retries+1))/$MAX_RETRIES)..."
        if docker pull --platform linux/amd64 "$image" 2>&1; then
            log_success "$image 拉取成功"
            return 0
        fi

        retries=$((retries+1))
        if [ $retries -lt $MAX_RETRIES ]; then
            log_warn "拉取失败，${RETRY_DELAY}秒后重试..."
            sleep $RETRY_DELAY
        fi
    done

    log_error "$image 拉取失败，已达到最大重试次数"
    return 1
}

# 创建输出目录（保留 nginx 和 cert 目录）
create_output_dir() {
    if [ -d "$OUTPUT_DIR" ]; then
        log_warn "输出目录已存在，将清理旧文件（保留 nginx 和 cert）"
        # 备份 nginx 和 cert 目录
        if [ -d "$OUTPUT_DIR/nginx" ]; then
            mv "$OUTPUT_DIR/nginx" /tmp/fruit-nginx-backup
        fi
        if [ -d "$OUTPUT_DIR/cert" ]; then
            mv "$OUTPUT_DIR/cert" /tmp/fruit-cert-backup
        fi
        rm -rf "$OUTPUT_DIR"
    fi
    mkdir -p "$OUTPUT_DIR"
    # 恢复 nginx 和 cert 目录
    if [ -d /tmp/fruit-nginx-backup ]; then
        mv /tmp/fruit-nginx-backup "$OUTPUT_DIR/nginx"
        log_success "恢复 nginx 配置目录"
    fi
    if [ -d /tmp/fruit-cert-backup ]; then
        mv /tmp/fruit-cert-backup "$OUTPUT_DIR/cert"
        log_success "恢复 cert 证书目录"
    fi
    log_success "创建输出目录: $OUTPUT_DIR"
}

# 预拉取基础镜像
pull_base_images() {
    log_info "预拉取基础镜像（目标平台: linux/amd64）..."
    for image in "${BASE_IMAGES[@]}"; do
        pull_with_retry "$image" || return 1
    done
    log_success "基础镜像预拉取完成"
}

# 构建后端镜像
build_backend() {
    log_info "开始构建后端镜像..."
    cd "${SCRIPT_DIR}/backend"
    docker build --platform linux/amd64 -t "$BACKEND_IMAGE" . 2>&1
    log_success "后端镜像构建完成: $BACKEND_IMAGE"
}

# 构建前端镜像
build_frontend() {
    log_info "开始构建前端镜像..."
    cd "${SCRIPT_DIR}/admin_web"
    docker build --platform linux/amd64 -t "$ADMIN_WEB_IMAGE" . 2>&1
    log_success "前端镜像构建完成: $ADMIN_WEB_IMAGE"
}

# 导出镜像（仅导出业务镜像，中间件服务器已有）
export_images() {
    log_info "开始导出镜像..."

    # 导出后端镜像
    log_info "导出后端镜像..."
    docker save "$BACKEND_IMAGE" -o "${OUTPUT_DIR}/fruit-backend.tar"

    # 导出前端镜像
    log_info "导出前端镜像..."
    docker save "$ADMIN_WEB_IMAGE" -o "${OUTPUT_DIR}/fruit-admin-web.tar"

    log_success "所有镜像导出完成"
}

# 复制配置文件
copy_configs() {
    log_info "复制配置文件..."

    # 复制生产环境 docker-compose（使用预构建镜像）
    cp "${SCRIPT_DIR}/docker-compose.prod.yaml" "${OUTPUT_DIR}/docker-compose.yaml"

    # 复制数据库初始化脚本
    cp "${SCRIPT_DIR}/database_design.sql" "${OUTPUT_DIR}/" 2>/dev/null || log_warn "database_design.sql 不存在"
    cp "${SCRIPT_DIR}/init_data.sql" "${OUTPUT_DIR}/" 2>/dev/null || log_warn "init_data.sql 不存在"

    # 复制导入脚本
    cp "${SCRIPT_DIR}/import-images.sh" "${OUTPUT_DIR}/" 2>/dev/null || log_warn "import-images.sh 不存在"

    # 检查 nginx 和 cert 目录是否存在
    if [ -d "${OUTPUT_DIR}/nginx" ]; then
        log_success "nginx 配置目录已存在"
    else
        log_warn "nginx 目录不存在，请手动创建"
    fi

    if [ -d "${OUTPUT_DIR}/cert" ]; then
        log_success "cert 证书目录已存在"
    else
        log_warn "cert 目录不存在，请手动添加 SSL 证书"
    fi

    log_success "配置文件复制完成"
}

# 创建压缩包
create_archive() {
    log_info "创建压缩包..."
    cd "${SCRIPT_DIR}"
    tar -czvf "$ARCHIVE_NAME" -C "$(dirname $OUTPUT_DIR)" "$(basename $OUTPUT_DIR)"

    # 计算文件大小
    SIZE=$(du -h "$ARCHIVE_NAME" | cut -f1)
    log_success "压缩包创建完成: ${ARCHIVE_NAME} (${SIZE})"
}

# 显示结果
show_result() {
    echo ""
    echo "=========================================="
    echo -e "${GREEN}打包完成！${NC}"
    echo "=========================================="
    echo ""
    echo "输出目录: ${OUTPUT_DIR}"
    echo "压缩包: ${SCRIPT_DIR}/${ARCHIVE_NAME}"
    echo ""
    echo "目录内容:"
    ls -lh "${OUTPUT_DIR}"
    echo ""
    echo "使用方法:"
    echo "  1. 将 ${ARCHIVE_NAME} 复制到目标服务器"
    echo "  2. 解压: tar -xzvf ${ARCHIVE_NAME}"
    echo "  3. 进入目录: cd docker-images"
    echo "  4. 执行导入: ./import-images.sh"
    echo "  5. 启动服务: docker-compose up -d"
    echo ""
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help       显示帮助信息"
    echo "  -s, --sequential 顺序构建（不并行，更稳定）"
    echo "  --skip-pull      跳过预拉取基础镜像"
    echo ""
}

# 主函数
main() {
    local sequential=false
    local skip_pull=false

    # 解析参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -s|--sequential)
                sequential=true
                shift
                ;;
            --skip-pull)
                skip_pull=true
                shift
                ;;
            *)
                log_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done

    echo ""
    echo "=========================================="
    echo "  水果销售平台 Docker 镜像打包工具"
    echo "=========================================="
    echo ""

    check_docker
    create_output_dir

    # 预拉取基础镜像（避免构建时的速率限制）
    if [ "$skip_pull" = false ]; then
        pull_base_images || { log_error "基础镜像拉取失败"; exit 1; }
    fi

    if [ "$sequential" = true ]; then
        # 顺序构建
        log_info "顺序构建模式..."
        build_backend || { log_error "后端构建失败"; exit 1; }
        build_frontend || { log_error "前端构建失败"; exit 1; }
    else
        # 并行构建
        log_info "并行构建模式..."

        build_backend &
        PID_BACKEND=$!
        build_frontend &
        PID_FRONTEND=$!

        # 等待所有构建完成
        wait $PID_BACKEND || { log_error "后端构建失败"; exit 1; }
        wait $PID_FRONTEND || { log_error "前端构建失败"; exit 1; }
    fi

    export_images
    copy_configs
    create_archive
    show_result
}

# 执行
main "$@"
