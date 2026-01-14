package com.fruit.sale.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fruit.sale.config.MinioProperties;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.service.MinioService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MinIO 文件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadUserImage(MultipartFile file) {
        return uploadFile(file, minioProperties.getBuckets().getUserInfo());
    }

    @Override
    public String uploadProductImage(MultipartFile file) {
        return uploadFile(file, minioProperties.getBuckets().getProductInfo());
    }

    @Override
    public String uploadSystemImage(MultipartFile file) {
        return uploadFile(file, minioProperties.getBuckets().getSystemInfo());
    }

    @Override
    public String uploadSystemVideo(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BusinessException("只支持上传视频文件");
        }

        // 验证文件大小（限制为100MB）
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            throw new BusinessException("视频文件大小不能超过100MB");
        }

        return uploadFile(file, minioProperties.getBuckets().getSystemInfo());
    }

    @Override
    public String uploadShippingImage(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只支持上传图片文件");
        }

        // 验证文件大小（限制为10MB）
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new BusinessException("图片文件大小不能超过10MB");
        }

        return uploadFile(file, minioProperties.getBuckets().getSystemInfo());
    }

    /**
     * 上传文件到指定桶
     */
    private String uploadFile(MultipartFile file, String bucketName) {
        try {
            // 确保桶存在
            ensureBucketExists(bucketName);

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new BusinessException("文件名不能为空");
            }

            // 生成唯一文件名：日期/UUID_原始文件名
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = IdUtil.simpleUUID() + "_" + originalFilename;
            String objectName = datePath + "/" + fileName;

            // 上传文件
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            inputStream.close();

            // 返回文件访问URL
            return getFileUrl(bucketName, objectName);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 确保桶存在，不存在则创建
     */
    private void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                // 创建桶
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );

                // 设置桶策略为公开读
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build()
                );

                log.info("创建桶成功: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("检查/创建桶失败: {}", bucketName, e);
            throw new BusinessException("桶操作失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            log.info("删除文件成功: {}/{}", bucketName, objectName);
        } catch (Exception e) {
            log.error("删除文件失败: {}/{}", bucketName, objectName, e);
            throw new BusinessException("删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String bucketName, String objectName) {
        // 返回文件的公开访问URL（使用配置的公开地址）
        String baseUrl = minioProperties.getPublicUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            // 如果没有配置公开地址，使用 endpoint
            baseUrl = minioProperties.getEndpoint();
        }
        return baseUrl + "/" + bucketName + "/" + objectName;
    }
}
