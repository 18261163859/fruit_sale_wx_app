package com.fruit.sale.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * MinIO 服务地址（内部访问）
     */
    private String endpoint;

    /**
     * 公开访问地址（返回给客户端）
     */
    private String publicUrl;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 桶配置
     */
    private Buckets buckets;

    @Data
    public static class Buckets {
        private String userInfo;
        private String productInfo;
        private String systemInfo;
    }
}
