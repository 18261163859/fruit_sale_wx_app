package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.VipOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 星享会员开通订单 Mapper
 *
 * @author fruit-sale
 * @since 2025-01-09
 */
@Mapper
public interface VipOrderMapper extends BaseMapper<VipOrder> {
}
