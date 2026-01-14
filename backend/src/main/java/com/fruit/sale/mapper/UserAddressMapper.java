package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收货地址 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
