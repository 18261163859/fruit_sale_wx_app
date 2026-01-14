package com.fruit.sale.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO 文件服务接口
 */
public interface MinioService {

    /**
     * 上传文件到用户信息桶
     */
    String uploadUserImage(MultipartFile file);

    /**
     * 上传文件到商品信息桶
     */
    String uploadProductImage(MultipartFile file);

    /**
     * 上传文件到系统信息桶
     */
    String uploadSystemImage(MultipartFile file);

    /**
     * 上传视频文件到系统信息桶
     */
    String uploadSystemVideo(MultipartFile file);

    /**
     * 上传发货图片（打包照片等）
     */
    String uploadShippingImage(MultipartFile file);

    /**
     * 删除文件
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 获取文件访问URL
     */
    String getFileUrl(String bucketName, String objectName);
}
