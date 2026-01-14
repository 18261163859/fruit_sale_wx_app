package com.fruit.sale.controller;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.Result;
import com.fruit.sale.dto.ProductQueryDTO;
import com.fruit.sale.dto.ProductSpecDTO;
import com.fruit.sale.entity.ProductCategory;
import com.fruit.sale.service.IAdminService;
import com.fruit.sale.service.IProductService;
import com.fruit.sale.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "商品管理", description = "商品相关接口")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IAdminService adminService;

    @Operation(summary = "分页查询商品", description = "支持按名称、分类、状态等条件查询")
    @PostMapping("/page")
    public Result<PageResult<ProductVO>> pageQuery(@RequestBody ProductQueryDTO queryDTO) {
        PageResult<ProductVO> page = productService.pageQuery(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情", description = "根据商品ID获取详细信息")
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductDetail(@PathVariable Long productId) {
        ProductVO product = productService.getProductDetail(productId);
        return Result.success(product);
    }

    @Operation(summary = "获取推荐商品", description = "获取首页推荐的商品列表")
    @GetMapping("/recommend")
    public Result<List<ProductVO>> getRecommendProducts() {
        List<ProductVO> products = productService.getRecommendProducts();
        return Result.success(products);
    }

    @Operation(summary = "根据分类获取商品", description = "获取指定分类下的所有商品")
    @GetMapping("/category/{categoryId}")
    public Result<List<ProductVO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductVO> products = productService.getProductsByCategory(categoryId);
        return Result.success(products);
    }

    @Operation(summary = "获取所有分类", description = "获取商品分类列表")
    @GetMapping("/category/list")
    public Result<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = productService.getAllCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取商品规格列表", description = "获取指定商品的所有规格")
    @GetMapping("/{productId}/specs")
    public Result<List<ProductSpecDTO>> getProductSpecs(@PathVariable Long productId) {
        List<ProductSpecDTO> specs = adminService.getProductSpecs(productId);
        return Result.success(specs);
    }
}
