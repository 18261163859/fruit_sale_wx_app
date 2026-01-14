package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}