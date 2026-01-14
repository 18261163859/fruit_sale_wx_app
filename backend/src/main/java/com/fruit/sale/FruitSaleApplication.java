package com.fruit.sale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 高端云南水果销售系统启动类
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@SpringBootApplication
@MapperScan("com.fruit.sale.mapper")
public class FruitSaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FruitSaleApplication.class, args);
        System.out.println("""
                
                ========================================
                   高端云南水果销售系统启动成功！
                   API 文档地址: http://localhost:8000/api/doc.html
                   Druid 监控: http://localhost:8000/api/druid
                ========================================
                """);
    }
}
