#!/bin/bash

# ===========================================
# 水果销售平台 镜像导入脚本（服务器端）
# 用于更新已有环境的镜像
# ===========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ARCHIVE_NAME=""

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

# 查找最新的压缩包
find_latest_archive() {
    local archives=($(ls -t ${SCRIPT_DIR}/fruit-images-*.tar.gz 2>/dev/null))
    if [ ${#archives[@]} -eq 0 ]; then
        log_error "未找到镜像压缩包"
        exit 1
    fi
    ARCHIVE_NAME="${SCRIPT_DIR}/${archives[0]}"
    log_info "使用最新的压缩包: $(basename $ARCHIVE_NAME)"
}

# 解压镜像
extract_images() {
    log_info "解压镜像..."
    tar -xzvf "$ARCHIVE_NAME" -C "$SCRIPT_DIR"
    log_success "解压完成"
}

# 导入镜像
load_images() {
    log_info "导入镜像..."
    local image_file="${SCRIPT_DIR}/docker-images/fruit-images.tar"
    
    if [ ! -f "$image_file" ]; then
        log_error "未找到镜像文件: $image_file"
        exit 1
    fi
    
    docker load -i "$image_file"
    log_success "镜像导入完成"
}

# 显示镜像
show_images() {
    echo ""
    echo "=========================================="
    echo "  当前镜像列表"
    echo "=========================================="
    docker images | grep -E "fruit-sale|backend|admin-web" || echo "未找到相关镜像"
    echo ""
}

# 重启服务
restart_services() {
    log_info "重启服务..."
    cd "$SCRIPT_DIR"
    
    if [ ! -f "docker-compose.yaml" ]; then
        log_warn "docker-compose.yaml 不存在，跳过服务重启"
        log_info "请手动执行: cd $SCRIPT_DIR && docker-compose up -d"
        return 0
    fi
    
    docker-compose down --remove-orphans 2>/dev/null || true
    docker-compose pull
    docker-compose up -d
    
    log_success "服务已重启"
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help       显示帮助"
    echo "  --extract-only   仅解压，不导入"
    echo "  --load-only      仅导入，不重启"
    echo "  --restart        重启服务（默认行为）"
    echo ""
    echo "示例:"
    echo "  $0               # 解压、导入、重启一气呵成"
    echo "  $0 --restart     # 仅重启服务（假设已解压）"
    echo "  $0 --extract-only # 仅解压压缩包"
    echo ""
}

# 主函数
main() {
    local extract_only=false
    local load_only=false
    local restart=true

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help) show_help; exit 0 ;;
            --extract-only) extract_only=true; shift ;;
            --load-only) load_only=true; restart=false; shift ;;
            --restart) restart=true; shift ;;
            *) log_error "未知参数: $1"; exit 1 ;;
        esac
    done

    echo "=========================================="
    echo "  水果销售平台 镜像导入工具（服务器端）"
    echo "=========================================="
    echo ""

    check_docker

    if [ "$load_only" = false ]; then
        find_latest_archive
        extract_images
    fi

    if [ "$extract_only" = true ]; then
        log_info "解压完成，可执行 $0 --load-only 继续导入"
        exit 0
    fi

    load_images
    show_images

    if [ "$restart" = true ]; then
        restart_services
    else
        log_info "可执行 $0 --restart 重启服务"
    fi

    echo ""
    echo "=========================================="
    echo "  完成!"
    echo "=========================================="
    echo ""
}

main "$@"