package com.fruit.sale.service;

import com.fruit.sale.dto.AdminLoginDTO;
import com.fruit.sale.vo.LoginVO;

/**
 * 管理员服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IAdminUserService {

    /**
     * 管理员登录
     */
    LoginVO login(AdminLoginDTO loginDTO);
}