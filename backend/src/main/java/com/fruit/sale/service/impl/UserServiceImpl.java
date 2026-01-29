package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.common.ResultCode;
import com.fruit.sale.dto.LoginDTO;
import com.fruit.sale.entity.IntegralCard;
import com.fruit.sale.entity.IntegralRecord;
import com.fruit.sale.entity.UserInfo;
import com.fruit.sale.enums.OrderStatusEnum;
import com.fruit.sale.enums.UserTypeEnum;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.IntegralCardMapper;
import com.fruit.sale.mapper.IntegralRecordMapper;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IUserService;
import com.fruit.sale.utils.JwtUtils;
import com.fruit.sale.vo.LoginVO;
import com.fruit.sale.vo.UserInfoVO;
import com.fruit.sale.vo.IntegralRecordVO;
import com.fruit.sale.vo.OrderStatisticsVO;
import com.fruit.sale.entity.OrderInfo;
import com.fruit.sale.mapper.OrderInfoMapper;
import com.fruit.sale.entity.AgentApplication;
import com.fruit.sale.mapper.AgentApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IntegralCardMapper integralCardMapper;

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private com.fruit.sale.utils.WeChatUtil weChatUtil;

    @Autowired
    private com.fruit.sale.utils.WeChatApiUtil weChatApiUtil;

    @Autowired
    private AgentApplicationMapper agentApplicationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginDTO loginDTO) {
        // 调用微信API获取真实的openid和unionid
        cn.hutool.json.JSONObject wxData = weChatApiUtil.getOpenIdAndUnionId(loginDTO.getCode());
        String openid = wxData.getStr("openid");
        String unionid = wxData.getStr("unionid", "");

        UserInfo user = null;

        // 获取手机号：优先使用phoneCode解密，其次使用传入的phone
        String phone = loginDTO.getPhone();
        boolean isPhoneCodeLogin = false; // 标记是否是通过微信授权获取的手机号
        if (loginDTO.getPhoneCode() != null && !loginDTO.getPhoneCode().isEmpty()) {
            // 使用phoneCode通过微信API获取真实手机号
            phone = weChatUtil.getPhoneNumber(loginDTO.getPhoneCode());
            isPhoneCodeLogin = true;
        }

        // 判断是否是手机号直接登录（测试环境）
        boolean isDirectPhoneLogin = !isPhoneCodeLogin && phone != null && !phone.isEmpty()
                && !phone.startsWith("wx_") && !phone.matches("138\\*\\*\\*\\*\\d+");

        // 【核心逻辑区分】
        if (isPhoneCodeLogin) {
            // ========== 微信授权登录（获取手机号按钮）==========
            // 优先通过openid查找用户（同一个微信用户的openid是唯一的）
            log.info("微信授权登录: openid={}, phone={}", openid, phone);

            if (openid != null && !openid.isEmpty()) {
                user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getOpenid, openid));

                if (user != null) {
                    log.info("通过openid找到用户: userId={}, 原手机号={}", user.getId(), user.getPhone());
                }
            }

            // 如果openid没找到，再通过手机号查找
            if (user == null && phone != null && !phone.isEmpty()) {
                user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getPhone, phone));

                if (user != null) {
                    log.info("通过手机号找到用户: userId={}, openid={}", user.getId(), user.getOpenid());
                }
            }
        } else if (isDirectPhoneLogin) {
            // ========== 手机号测试登录 ==========
            // 只通过手机号查找，不使用openid
            log.info("手机号测试登录: phone={}", phone);

            user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getPhone, phone));

            if (user != null) {
                log.info("通过手机号找到用户: userId={}", user.getId());
            }

            // 为测试登录生成mock openid
            openid = "test_openid_" + phone;
            unionid = "";
        } else {
            // 其他情况：通过openid查找
            if (openid != null && !openid.isEmpty()) {
                user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getOpenid, openid));
            }
        }

        // 如果找到了用户，更新openid、unionid、昵称和头像
        if (user != null) {
            boolean needUpdate = false;

            // 更新openid和unionid
            if (isPhoneCodeLogin) {
                // 微信授权登录：可以更新openid和unionid
                if (openid != null && !openid.equals(user.getOpenid())) {
                    user.setOpenid(openid);
                    needUpdate = true;
                    log.info("更新用户{}的openid: {} -> {}", user.getId(), user.getOpenid(), openid);
                }
                if (unionid != null && !unionid.isEmpty() && !unionid.equals(user.getUnionid())) {
                    user.setUnionid(unionid);
                    needUpdate = true;
                    log.info("更新用户{}的unionid: {} -> {}", user.getId(), user.getUnionid(), unionid);
                }
            } else if (isDirectPhoneLogin) {
                // 手机号测试登录：不更新openid，除非用户原本就没有真实openid
                if (user.getOpenid() == null || user.getOpenid().isEmpty() || user.getOpenid().startsWith("test_openid_")) {
                    user.setOpenid(openid);
                    needUpdate = true;
                    log.info("测试登录：设置用户{}的mock openid: {}", user.getId(), openid);
                } else {
                    log.info("测试登录：保留用户{}的真实openid: {}", user.getId(), user.getOpenid());
                }
            }

            // 更新手机号（只有微信授权登录获取到真实手机号时才更新）
            if (isPhoneCodeLogin && phone != null && !phone.isEmpty() && !phone.equals(user.getPhone())) {
                // 检查新手机号是否已被其他用户使用
                UserInfo phoneUser = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getPhone, phone)
                        .ne(UserInfo::getId, user.getId()));

                if (phoneUser != null) {
                    log.warn("手机号{}已被其他用户(ID={})使用，无法更新用户{}的手机号", phone, phoneUser.getId(), user.getId());
                } else {
                    log.info("更新用户{}手机号: {} -> {}", user.getId(), user.getPhone(), phone);
                    user.setPhone(phone);
                    needUpdate = true;
                }
            }

            // 更新昵称和头像（如果传了）
            if (loginDTO.getNickname() != null && !loginDTO.getNickname().isEmpty() && !loginDTO.getNickname().equals(user.getNickname())) {
                user.setNickname(loginDTO.getNickname());
                needUpdate = true;
            }
            if (loginDTO.getAvatar() != null && !loginDTO.getAvatar().isEmpty() && !loginDTO.getAvatar().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(loginDTO.getAvatar());
                needUpdate = true;
            }

            if (needUpdate) {
                userInfoMapper.updateById(user);
            }
        }

        if (user == null) {
            // 新用户注册
            // 如果前端传了手机号就用前端的，否则用临时手机号
            if (phone == null || phone.isEmpty()) {
                phone = "wx_" + System.currentTimeMillis();
            }

            // 在插入前再次检查手机号是否已存在（防止并发或数据不一致导致的冲突）
            UserInfo existingUser = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getPhone, phone));

            if (existingUser != null) {
                // 手机号已存在，使用现有用户并更新openid
                log.info("手机号{}已存在，使用现有用户ID={}", phone, existingUser.getId());
                user = existingUser;
                boolean needUpdate = false;

                if (openid != null && !openid.equals(user.getOpenid())) {
                    user.setOpenid(openid);
                    needUpdate = true;
                }
                if (unionid != null && !unionid.isEmpty() && !unionid.equals(user.getUnionid())) {
                    user.setUnionid(unionid);
                    needUpdate = true;
                }
                if (loginDTO.getNickname() != null && !loginDTO.getNickname().equals(user.getNickname())) {
                    user.setNickname(loginDTO.getNickname());
                    needUpdate = true;
                }
                if (loginDTO.getAvatar() != null && !loginDTO.getAvatar().equals(user.getAvatarUrl())) {
                    user.setAvatarUrl(loginDTO.getAvatar());
                    needUpdate = true;
                }

                if (needUpdate) {
                    userInfoMapper.updateById(user);
                }
            } else {
                // 真正的新用户，创建用户记录
                // 生成用户编号：U + YYYYMMDD + 3位数字
                String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                Long maxId = userInfoMapper.selectCount(null);
                String userNo = String.format("U%s%03d", dateStr, (maxId + 1) % 1000);

                // 处理邀请码，查找邀请人
                Long inviterId = loginDTO.getInviterId();
                boolean isAgentInvitation = false;

                if (loginDTO.getAgentInviteCode() != null && !loginDTO.getAgentInviteCode().isEmpty()) {
                    UserInfo inviter = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviteCode, loginDTO.getAgentInviteCode()));
                    if (inviter != null) {
                        inviterId = inviter.getId();
                        isAgentInvitation = true;
                        log.info("Found inviter via agent invite code: {}", loginDTO.getAgentInviteCode());
                    } else {
                        log.warn("Invalid agent invite code: {}", loginDTO.getAgentInviteCode());
                    }
                }
                else if (loginDTO.getInviteCode() != null && !loginDTO.getInviteCode().isEmpty()) {
                    UserInfo inviter = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                            .eq(UserInfo::getInviteCode, loginDTO.getInviteCode()));
                    if (inviter != null) {
                        inviterId = inviter.getId();
                        log.info("Found inviter via invite code: {}", loginDTO.getInviteCode());
                    } else {
                        log.warn("Invalid invite code: {}", loginDTO.getInviteCode());
                    }
                }

                user = new UserInfo();
                user.setUserNo(userNo);
                user.setPhone(phone);
                user.setOpenid(openid);
                user.setUnionid(unionid);
                user.setNickname(loginDTO.getNickname());
                user.setAvatarUrl(loginDTO.getAvatar());
                user.setUserType(1); // 1-普通会员
                user.setInviterUserId(inviterId);
                user.setTotalConsumeAmount(BigDecimal.ZERO);
                user.setIntegralBalance(0);
                user.setVipExpireTime(null); // 非星享会员
                user.setStatus(1); // 默认启用

                // 生成唯一邀请码：6位大写字母+数字
                String inviteCode = generateInviteCode();
                user.setInviteCode(inviteCode);

                userInfoMapper.insert(user);
                log.info("创建新用户，手机号={}，用户ID={}，邀请码={}，邀请人ID={}", phone, user.getId(), inviteCode, inviterId);
            }
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系客服");
        }

        // 生成Token
        String token = JwtUtils.generateToken(user.getId());

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setToken(token);
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatarUrl());
        loginVO.setUserType(UserTypeEnum.getByCode(user.getUserType()));
        // 判断是否为星享会员：通过vip_expire_time字段，而不是user_type
        loginVO.setIsStarMember((user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) ? 1 : 0);
        loginVO.setPhone(user.getPhone());
        loginVO.setIntegral(user.getIntegralBalance());
        loginVO.setBalance(user.getTotalConsumeAmount().toString());

        return loginVO;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        UserInfoVO vo = BeanUtil.copyProperties(user, UserInfoVO.class);

        // 格式化VIP过期时间
        if (user.getVipExpireTime() != null) {
            vo.setVipExpireTime(user.getVipExpireTime().toString());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openStarMember(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 通过vip_expire_time判断是否已经是星享会员
        if (user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("您已是星享会员");
        }

        // 检查余额是否足够
        if (user.getTotalConsumeAmount().compareTo(new BigDecimal("199")) < 0) {
            throw new BusinessException("余额不足，需要199元");
        }

        // 扣除费用并升级为星享会员
        user.setTotalConsumeAmount(user.getTotalConsumeAmount().subtract(new BigDecimal("199")));
        user.setUserType(2); // 2-VIP会员
        user.setVipExpireTime(LocalDateTime.now().plusYears(1)); // 1年有效期
        // agentLevel 保持不变，VIP和代理身份独立

        userInfoMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rechargeIntegral(Long userId, String cardNo) {
        // 查询积分卡（使用兑换码字段查询）
        IntegralCard card = integralCardMapper.selectOne(new LambdaQueryWrapper<IntegralCard>()
                .eq(IntegralCard::getCardCode, cardNo));

        if (card == null) {
            throw new BusinessException("兑换码不存在");
        }

        if (card.getCardStatus() == 1) {
            throw new BusinessException("兑换码已使用");
        }

        if (card.getExpireTime() != null && card.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("兑换码已过期");
        }

        // 更新用户积分
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        Integer balanceBefore = user.getIntegralBalance();
        Integer balanceAfter = balanceBefore + card.getIntegralAmount();
        user.setIntegralBalance(balanceAfter);
        userInfoMapper.updateById(user);

        // 更新积分卡状态
        card.setCardStatus(1); // 1-已使用
        card.setUseUserId(userId);
        card.setUseTime(LocalDateTime.now());
        integralCardMapper.updateById(card);

        // 记录积分变动
        IntegralRecord record = new IntegralRecord();
        record.setRecordNo("IR" + System.currentTimeMillis());
        record.setUserId(userId);
        record.setChangeType(1); // 1-充值
        record.setChangeAmount(card.getIntegralAmount());
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setRelatedCardNo(card.getCardNo());
        record.setRemark("兑换卡充值");
        integralRecordMapper.insert(record);
    }

    @Override
    public UserInfo getById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    public void updatePhone(Long userId, String phone) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPhone(phone);
        userInfoMapper.updateById(user);
    }

    @Override
    public List<IntegralRecordVO> getIntegralRecords(Long userId) {
        // 查询用户的积分记录
        LambdaQueryWrapper<IntegralRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IntegralRecord::getUserId, userId)
               .orderByDesc(IntegralRecord::getCreateTime);

        List<IntegralRecord> records = integralRecordMapper.selectList(wrapper);

        // 转换为VO
        List<IntegralRecordVO> voList = new ArrayList<>();
        for (IntegralRecord record : records) {
            IntegralRecordVO vo = new IntegralRecordVO();
            vo.setId(record.getId());
            vo.setChangeType(record.getChangeType());
            vo.setChangeAmount(record.getChangeAmount());
            vo.setAfterBalance(record.getBalanceAfter());
            vo.setRemark(record.getRemark());
            vo.setCreateTime(record.getCreateTime() != null ?
                record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");

            // 设置类型文本
            switch (record.getChangeType()) {
                case 1:
                    vo.setChangeTypeText("兑换卡充值");
                    break;
                case 2:
                    vo.setChangeTypeText("购物获得");
                    break;
                case 3:
                    vo.setChangeTypeText("分享获得");
                    break;
                case 4:
                    vo.setChangeTypeText("消费抵扣");
                    break;
                case 5:
                    vo.setChangeTypeText("管理员调整");
                    break;
                default:
                    vo.setChangeTypeText("未知");
            }

            voList.add(vo);
        }

        return voList;
    }

    @Override
    public OrderStatisticsVO getOrderStatistics(Long userId) {
        OrderStatisticsVO vo = new OrderStatisticsVO();

        // 查询各状态订单数量
        // 0-待付款, 1-待发货, 2-已发货, 3-已完成, 4-已取消
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);

        List<OrderInfo> allOrders = orderInfoMapper.selectList(wrapper);

        long pendingPayment = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatusEnum.PENDING_PAYMENT).count();
        long pendingShipment = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatusEnum.PENDING_SHIPMENT).count();
        long shipped = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatusEnum.SHIPPED).count();
        long completed = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatusEnum.COMPLETED).count();

        vo.setPendingPaymentCount((int) pendingPayment);
        vo.setPendingShipmentCount((int) pendingShipment);
        vo.setShippedCount((int) shipped);
        vo.setCompletedCount((int) completed);

        // 获取用户信息中的累计消费和返现
        UserInfo user = userInfoMapper.selectById(userId);
        if (user != null) {
            vo.setTotalAmount(user.getTotalConsumeAmount() != null ? user.getTotalConsumeAmount() : BigDecimal.ZERO);
            vo.setTotalCommission(user.getTotalCommissionAmount() != null ? user.getTotalCommissionAmount() : BigDecimal.ZERO);
        } else {
            vo.setTotalAmount(BigDecimal.ZERO);
            vo.setTotalCommission(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindInviteCode(Long userId, String inviteCode) {
        // 1. 查询当前用户
        UserInfo currentUser = userInfoMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 检查当前用户是否已经绑定过邀请人
        if (currentUser.getInviterUserId() != null) {
            throw new BusinessException("您已绑定过邀请人，不可重复绑定");
        }

        // 3. 根据邀请码查询邀请人
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getInviteCode, inviteCode);
        UserInfo inviter = userInfoMapper.selectOne(wrapper);

        if (inviter == null) {
            throw new BusinessException("邀请码不存在");
        }

        // 4. 不能绑定自己
        if (inviter.getId().equals(userId)) {
            throw new BusinessException("不能绑定自己的邀请码");
        }

        // 5. 更新当前用户的邀请人信息
        currentUser.setInviterUserId(inviter.getId());

        userInfoMapper.updateById(currentUser);

        log.info("用户{}成功绑定邀请人{}的邀请码{}", userId, inviter.getId(), inviteCode);
    }

    /**
     * 生成唯一邀请码
     */
    private String generateInviteCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 去除易混淆字符
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();

        // 生成6位邀请码
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        // 检查是否重复
        String inviteCode = code.toString();
        UserInfo existingUser = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getInviteCode, inviteCode));

        // 如果重复，递归生成新的
        if (existingUser != null) {
            return generateInviteCode();
        }

        return inviteCode;
    }
}
