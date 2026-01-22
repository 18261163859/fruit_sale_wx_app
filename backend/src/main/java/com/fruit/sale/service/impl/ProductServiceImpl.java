package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.ResultCode;
import com.fruit.sale.dto.ProductQueryDTO;
import com.fruit.sale.entity.ProductCategory;
import com.fruit.sale.entity.ProductInfo;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.ProductCategoryMapper;
import com.fruit.sale.mapper.ProductInfoMapper;
import com.fruit.sale.service.IProductService;
import com.fruit.sale.service.ISystemConfigService;
import com.fruit.sale.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ISystemConfigService systemConfigService;

    /**
     * 获取VIP折扣率（例如：9.5折返回0.95）
     */
    private BigDecimal getVipDiscountRate() {
        try {
            String vipDiscount = systemConfigService.getConfigValue("vipDiscount");
            if (vipDiscount != null) {
                // 配置值是9.5表示95折，需要除以10得到0.95
                return new BigDecimal(vipDiscount).divide(new BigDecimal("10"));
            }
        } catch (Exception e) {
            log.error("获取VIP折扣配置失败", e);
        }
        // 默认95折
        return new BigDecimal("0.95");
    }

    /**
     * 计算并设置VIP价格
     */
    private void calculateVipPrice(ProductVO vo) {
        BigDecimal vipDiscountRate = getVipDiscountRate();
        vo.setVipPrice(vo.getPrice().multiply(vipDiscountRate));
    }

    @Override
    public PageResult<ProductVO> pageQuery(ProductQueryDTO queryDTO) {
        Page<ProductInfo> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<ProductInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getProductName()), 
                ProductInfo::getProductName, queryDTO.getProductName())
                .eq(queryDTO.getCategoryId() != null, 
                ProductInfo::getCategoryId, queryDTO.getCategoryId())
                .eq(queryDTO.getStatus() != null, 
                ProductInfo::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getIsRecommend() != null, 
                ProductInfo::getIsRecommend, queryDTO.getIsRecommend())
                .orderByDesc(ProductInfo::getSortOrder)
                .orderByDesc(ProductInfo::getCreateTime);

        Page<ProductInfo> productPage = productInfoMapper.selectPage(page, wrapper);

        // 获取分类信息
        List<Long> categoryIds = productPage.getRecords().stream()
                .map(ProductInfo::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> categoryMap = categoryIds.isEmpty()
                ? Map.of()
                : productCategoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getCategoryName));

        // 转换为VO
        List<ProductVO> voList = productPage.getRecords().stream().map(product -> {
            ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
            vo.setCategoryName(categoryMap.get(product.getCategoryId()));
            // 使用mainImage作为图片数组（database没有images字段）
            if (StrUtil.isNotBlank(product.getMainImage())) {
                vo.setImages(new String[]{product.getMainImage()});
            }
            // 计算VIP价格
            calculateVipPrice(vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.build(voList, productPage.getTotal(), 
                productPage.getCurrent(), productPage.getSize());
    }

    @Override
    public ProductVO getProductDetail(Long productId) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);

        // 手动映射 productDetail -> detail（字段名不一致）
        vo.setDetail(product.getProductDetail());

        // 获取分类名称
        ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        // 处理图片（database没有images字段，使用mainImage）
        if (StrUtil.isNotBlank(product.getMainImage())) {
            vo.setImages(new String[]{product.getMainImage()});
        }

        // 计算VIP价格
        calculateVipPrice(vo);

        return vo;
    }

    @Override
    public List<ProductVO> getRecommendProducts() {
        List<ProductInfo> products = productInfoMapper.selectList(
                new LambdaQueryWrapper<ProductInfo>()
                        .eq(ProductInfo::getStatus, 1)
                        .eq(ProductInfo::getIsRecommend, 1)
                        .orderByDesc(ProductInfo::getSortOrder)
                        .last("LIMIT 10"));

        return products.stream()
                .map(product -> {
                    ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
                    calculateVipPrice(vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> getProductsByCategory(Long categoryId) {
        List<ProductInfo> products = productInfoMapper.selectList(
                new LambdaQueryWrapper<ProductInfo>()
                        .eq(ProductInfo::getCategoryId, categoryId)
                        .eq(ProductInfo::getStatus, 1)
                        .orderByDesc(ProductInfo::getSortOrder));

        return products.stream()
                .map(product -> {
                    ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
                    calculateVipPrice(vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> getAllCategories() {
        return productCategoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getStatus, 1)
                        .orderByAsc(ProductCategory::getSortOrder));
    }

    @Override
    public void createCategory(ProductCategory category) {
        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        productCategoryMapper.insert(category);
    }

    @Override
    public void updateCategory(ProductCategory category) {
        ProductCategory existing = productCategoryMapper.selectById(category.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        productCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 检查是否有商品使用此分类
        Long count = productInfoMapper.selectCount(
                new LambdaQueryWrapper<ProductInfo>()
                        .eq(ProductInfo::getCategoryId, categoryId));
        if (count > 0) {
            throw new BusinessException("该分类下还有商品，无法删除");
        }

        ProductCategory category = productCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 使用 deleteById 触发 MyBatis Plus 的逻辑删除
        productCategoryMapper.deleteById(categoryId);
    }

    @Override
    public void saveOrUpdate(ProductInfo product) {
        if (product.getId() == null) {
            productInfoMapper.insert(product);
        } else {
            productInfoMapper.updateById(product);
        }
    }

    @Override
    public void updateStatus(Long productId, Integer status) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productInfoMapper.updateById(product);
    }

    @Override
    public void updateRecommend(Long productId, Integer isRecommend) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setIsRecommend(isRecommend);
        productInfoMapper.updateById(product);
    }
}
