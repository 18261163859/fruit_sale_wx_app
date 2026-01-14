package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品信息 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {
}
