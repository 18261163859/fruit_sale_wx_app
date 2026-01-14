package com.fruit.sale.service;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.dto.ProductQueryDTO;
import com.fruit.sale.entity.ProductCategory;
import com.fruit.sale.entity.ProductInfo;
import com.fruit.sale.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IProductService {

    /**
     * 分页查询商品
     */
    PageResult<ProductVO> pageQuery(ProductQueryDTO queryDTO);

    /**
     * 获取商品详情
     */
    ProductVO getProductDetail(Long productId);

    /**
     * 获取推荐商品列表
     */
    List<ProductVO> getRecommendProducts();

    /**
     * 根据分类获取商品
     */
    List<ProductVO> getProductsByCategory(Long categoryId);

    /**
     * 获取所有分类
     */
    List<ProductCategory> getAllCategories();

    /**
     * 创建分类
     */
    void createCategory(ProductCategory category);

    /**
     * 更新分类
     */
    void updateCategory(ProductCategory category);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 保存或更新商品
     */
    void saveOrUpdate(ProductInfo product);

    /**
     * 上下架商品
     */
    void updateStatus(Long productId, Integer status);

    /**
     * 设置推荐商品
     */
    void updateRecommend(Long productId, Integer isRecommend);
}
