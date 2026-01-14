package com.fruit.sale.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.fruit.sale.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 仅用于调试
 */
@Tag(name = "测试接口", description = "调试用临时接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "测试BCrypt密码")
    @GetMapping("/bcrypt")
    public Result<Map<String, Object>> testBCrypt(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        // 生成新的hash
        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
        result.put("newHash", newHash);

        // 测试新hash
        boolean check1 = BCrypt.checkpw(password, newHash);
        result.put("newHashCheck", check1);

        // 测试数据库中的hash
        String dbHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIzYoAYFJgA8HGF/a";
        boolean check2 = BCrypt.checkpw(password, dbHash);
        result.put("dbHashCheck", check2);

        // 测试我们生成的hash
        String testHash = "$2a$10$Aq2WZF6Htp6/cZ0jb7F5yOX.Mx9K3qVYPqQXmE1WdYL8R3wK4gNd6";
        boolean check3 = BCrypt.checkpw(password, testHash);
        result.put("testHashCheck", check3);

        return Result.success(result);
    }

    @Operation(summary = "生成BCrypt密码hash")
    @PostMapping("/generate-hash")
    public Result<String> generateHash(@RequestParam String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        return Result.success("生成成功", hash);
    }
}