package com.fruit.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.sale.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台管理员 Mapper
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
