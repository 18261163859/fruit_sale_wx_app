package com.fruit.sale.service;

import com.fruit.sale.dto.LoginDTO;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.vo.IntegralRecordVO;
import com.fruit.sale.vo.LoginVO;
import com.fruit.sale.vo.OrderStatisticsVO;
import com.fruit.sale.vo.UserInfoVO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IUserService {

    /**
     * 微信登录/注册
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 开通星享会员
     */
    void openStarMember(Long userId);

    /**
     * 充值积分（兑换卡）
     */
    void rechargeIntegral(Long userId, String cardNo);

    /**
     * 获取积分记录列表
     */
    List<IntegralRecordVO> getIntegralRecords(Long userId);

    /**
     * 获取订单统计数据
     */
    OrderStatisticsVO getOrderStatistics(Long userId);

    /**
     * 根据ID获取用户
     */
    UserInfo getById(Long userId);

    /**
     * 更新用户手机号
     */
    void updatePhone(Long userId, String phone);

    /**
     * 绑定邀请码
     */
    void bindInviteCode(Long userId, String inviteCode);
}
