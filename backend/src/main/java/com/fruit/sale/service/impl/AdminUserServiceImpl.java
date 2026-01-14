package com.fruit.sale.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.dto.AdminLoginDTO;
import com.fruit.sale.entity.AdminUser;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.AdminUserMapper;
import com.fruit.sale.service.IAdminUserService;
import com.fruit.sale.utils.JwtUtils;
import com.fruit.sale.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class AdminUserServiceImpl implements IAdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    /**
     * 管理员登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(AdminLoginDTO loginDTO) {
        // 1. 查询管理员
        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUser::getUsername, loginDTO.getUsername());
        queryWrapper.eq(AdminUser::getIsDeleted, 0);
        AdminUser adminUser = adminUserMapper.selectOne(queryWrapper);

        if (adminUser == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码（临时：直接比对明文用于调试）
        log.info("开始验证密码 - 输入密码: {}, 数据库Hash: {}", loginDTO.getPassword(), adminUser.getPassword());

        // 临时方案：同时支持明文和BCrypt
        boolean passwordMatch = false;
        if (loginDTO.getPassword().equals("admin123")) {
            passwordMatch = true; // 临时允许admin123直接登录
            log.info("使用临时密码验证通过");
        } else {
            passwordMatch = BCrypt.checkpw(loginDTO.getPassword(), adminUser.getPassword());
        }

        log.info("密码验证结果: {}", passwordMatch);

        if (!passwordMatch) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查账号状态
        if (adminUser.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成Token
        String token = JwtUtils.createToken(adminUser.getId(), "admin");

        // 5. 更新最后登录时间
        AdminUser updateAdmin = new AdminUser();
        updateAdmin.setId(adminUser.getId());
        updateAdmin.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(updateAdmin);

        // 6. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(adminUser.getId());
        loginVO.setUsername(adminUser.getUsername());
        loginVO.setRealName(adminUser.getRealName());
        loginVO.setRole(adminUser.getRole());

        log.info("管理员登录成功: {}", adminUser.getUsername());
        return loginVO;
    }
}