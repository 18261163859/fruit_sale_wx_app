package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传管理")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MinioService minioService;

    @Operation(summary = "上传用户头像")
    @PostMapping("/user/avatar")
    public Result<Map<String, String>> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadUserImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传商品图片")
    @PostMapping("/product/image")
    public Result<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadProductImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传系统图片（轮播图、配置图等）")
    @PostMapping("/system/image")
    public Result<Map<String, String>> uploadSystemImage(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadSystemImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传系统视频（轮播图视频等）")
    @PostMapping("/system/video")
    public Result<Map<String, String>> uploadSystemVideo(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadSystemVideo(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传发货图片（打包照片等）")
    @PostMapping("/shipping/image")
    public Result<Map<String, String>> uploadShippingImage(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadShippingImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }
}
