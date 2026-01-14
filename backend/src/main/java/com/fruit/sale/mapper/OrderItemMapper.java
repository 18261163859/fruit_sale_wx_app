package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
