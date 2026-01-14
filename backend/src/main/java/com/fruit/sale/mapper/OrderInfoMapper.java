package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
