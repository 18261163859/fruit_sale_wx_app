package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruit.sale.common.PageResult;
import com.fruit.sale.dto.*;
import com.fruit.sale.entity.*;
import com.fruit.sale.enums.AgentApplyStatusEnum;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.*;
import com.fruit.sale.service.IAdminService;
import com.fruit.sale.service.ISystemConfigService;
import com.fruit.sale.vo.IntegralCardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AgentApplyMapper agentApplyMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private BannerConfigMapper bannerConfigMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private IntegralCardMapper integralCardMapper;

    @Autowired
    private ProductSpecMapper productSpecMapper;

    @Autowired
    private ISystemConfigService systemConfigService;

    /**
     * 获取VIP折扣率（例如：9.5折返回0.95）
     */
    private BigDecimal getVipDiscountRate() {
        try {
            String vipDiscount = systemConfigService.getConfigValue("vipDiscount");
            if (vipDiscount != null) {
                // 配置值是9.5表示95折，需要除以10得到0.95
                return new BigDecimal(vipDiscount).divide(new BigDecimal("10"));
            }
        } catch (Exception e) {
            log.error("获取VIP折扣配置失败", e);
        }
        // 默认95折
        return new BigDecimal("0.95");
    }

    /**
     * 计算并设置规格VIP价格
     */
    private void calculateSpecVipPrice(ProductSpecDTO dto) {
        BigDecimal vipDiscountRate = getVipDiscountRate();
        dto.setVipPrice(dto.getPrice().multiply(vipDiscountRate));
    }

    @Override
    public PageResult<UserInfo> getUserList(Integer page, Integer pageSize, String keyword, Integer agentLevel, Integer vipLevel) {
        Page<UserInfo> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(UserInfo::getNickname, keyword)
                    .or()
                    .like(UserInfo::getPhone, keyword));
        }

        // 代理等级筛选
        if (agentLevel != null) {
            if (agentLevel == 0) {
                // 非代理：agentLevel = 0 或 null
                wrapper.and(w -> w.eq(UserInfo::getAgentLevel, 0)
                        .or()
                        .isNull(UserInfo::getAgentLevel));
            } else if (agentLevel == 1) {
                // 一级代理：agentLevel = 1
                wrapper.eq(UserInfo::getAgentLevel, 1);
            } else if (agentLevel == 2) {
                // 二级代理：agentLevel = 2
                wrapper.eq(UserInfo::getAgentLevel, 2);
            }
        }

        // VIP等级筛选
        if (vipLevel != null) {
            if (vipLevel == 0) {
                // 普通用户：userType = 1 或 null
                wrapper.and(w -> w.eq(UserInfo::getUserType, 1)
                        .or()
                        .isNull(UserInfo::getUserType));
            } else if (vipLevel == 1) {
                // VIP会员：userType = 2
                wrapper.eq(UserInfo::getUserType, 2);
            }
        }

        wrapper.orderByDesc(UserInfo::getCreateTime);
        Page<UserInfo> resultPage = userInfoMapper.selectPage(pageParam, wrapper);

        return PageResult.build(resultPage.getRecords(), resultPage.getTotal(),
                resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserAgent(SetAgentDTO setAgentDTO) {
        UserInfo user = userInfoMapper.selectById(setAgentDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 必须是星享会员（通过vip_expire_time判断）
        if (user.getVipExpireTime() == null || !user.getVipExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("用户必须是星享会员才能设置为代理");
        }

        // 设置为一级代理
        if (setAgentDTO.getAgentLevel() == 2) { // DTO中2表示一级代理
            user.setAgentLevel(1); // 数据库中1表示一级代理
            user.setParentAgentId(null);
            user.setCommissionRate(setAgentDTO.getCommissionRate());

            // 生成邀请码（如果还没有）
            if (user.getInviteCode() == null || user.getInviteCode().isEmpty()) {
                user.setInviteCode(generateInviteCode());
            }
        }
        // 设置为二级代理
        else if (setAgentDTO.getAgentLevel() == 3) { // DTO中3表示二级代理
            if (setAgentDTO.getParentAgentId() == null) {
                throw new BusinessException("二级代理必须指定上级代理");
            }

            UserInfo parentAgent = userInfoMapper.selectById(setAgentDTO.getParentAgentId());
            if (parentAgent == null || parentAgent.getAgentLevel() == null || parentAgent.getAgentLevel() != 1) {
                throw new BusinessException("上级代理不存在或不是一级代理");
            }

            user.setAgentLevel(2); // 数据库中2表示二级代理
            user.setParentAgentId(setAgentDTO.getParentAgentId());
            user.setCommissionRate(setAgentDTO.getCommissionRate());

            // 生成邀请码（如果还没有）
            if (user.getInviteCode() == null || user.getInviteCode().isEmpty()) {
                user.setInviteCode(generateInviteCode());
            }
        } else {
            throw new BusinessException("代理级别错误");
        }

        userInfoMapper.updateById(user);
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        String code;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            code = "A" + cn.hutool.core.util.RandomUtil.randomNumbers(7);
            attempts++;

            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserInfo> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getInviteCode, code);
            Long count = userInfoMapper.selectCount(wrapper);

            if (count == 0) {
                return code;
            }

            if (attempts >= maxAttempts) {
                throw new BusinessException("生成邀请码失败，请重试");
            }
        } while (true);
    }

    @Override
    public PageResult<AgentApply> getAgentApplyList(Integer page, Integer pageSize, Integer status) {
        Page<AgentApply> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<AgentApply> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(AgentApply::getStatus, AgentApplyStatusEnum.values()[status]);
        }

        wrapper.orderByDesc(AgentApply::getCreateTime);
        Page<AgentApply> resultPage = agentApplyMapper.selectPage(pageParam, wrapper);

        return PageResult.build(resultPage.getRecords(), resultPage.getTotal(),
                resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditAgentApply(Long adminId, AgentApplyAuditDTO auditDTO) {
        AgentApply apply = agentApplyMapper.selectById(auditDTO.getApplyId());
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }

        if (apply.getStatus() != AgentApplyStatusEnum.PENDING) {
            throw new BusinessException("该申请已处理");
        }

        apply.setStatus(auditDTO.getStatus() == 1 ? AgentApplyStatusEnum.APPROVED : AgentApplyStatusEnum.REJECTED);
        apply.setReviewerId(adminId);
        apply.setReviewTime(LocalDateTime.now());
        apply.setReviewRemark(auditDTO.getReviewRemark());
        agentApplyMapper.updateById(apply);

        // 如果审批通过，更新用户信息
        if (auditDTO.getStatus() == 1) {
            UserInfo user = userInfoMapper.selectById(apply.getUserId());
            if (user != null) {
                if (apply.getApplyLevel() == 2) {
                    user.setAgentLevel(1);  // 一级代理（userType保持为2-星享会员）
                    user.setParentAgentId(null);
                } else if (apply.getApplyLevel() == 3) {
                    user.setAgentLevel(2);  // 二级代理（userType保持为2-星享会员）
                    user.setParentAgentId(apply.getRecommenderId());
                }
                user.setCommissionRate(apply.getCommissionRate());

                // 生成邀请码（如果还没有）
                if (user.getInviteCode() == null || user.getInviteCode().isEmpty()) {
                    user.setInviteCode(generateInviteCode());
                }

                userInfoMapper.updateById(user);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddDTO productAddDTO) {
        ProductInfo product = BeanUtil.copyProperties(productAddDTO, ProductInfo.class);

        // 手动处理字段名不一致的情况：DTO.detail -> Entity.productDetail
        if (productAddDTO.getDetail() != null) {
            product.setProductDetail(productAddDTO.getDetail());
        }

        // 生成商品编号 格式: P + 时间戳后8位 + 4位随机数
        String productNo = "P" + System.currentTimeMillis() % 100000000 +
                          String.format("%04d", (int)(Math.random() * 10000));
        product.setProductNo(productNo);
        product.setStatus(1); // 默认上架
        product.setSalesCount(0);

        productInfoMapper.insert(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductUpdateDTO productUpdateDTO) {
        ProductInfo product = productInfoMapper.selectById(productUpdateDTO.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        BeanUtil.copyProperties(productUpdateDTO, product);
        // 手动处理字段名不一致的情况：DTO.detail -> Entity.productDetail
        if (productUpdateDTO.getDetail() != null) {
            product.setProductDetail(productUpdateDTO.getDetail());
        }
        productInfoMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long productId, Integer status) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        productInfoMapper.updateById(product);
    }

    @Override
    public void updateProductSales(Long productId, Integer salesCount) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setSalesCount(salesCount);
        productInfoMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long productId) {
        ProductInfo product = productInfoMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productInfoMapper.deleteById(productId);
    }

    @Override
    public PageResult<OrderInfo> getOrderList(Integer page, Integer pageSize, Integer status) {
        Page<OrderInfo> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(OrderInfo::getOrderStatus, status);
        }

        wrapper.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> resultPage = orderInfoMapper.selectPage(pageParam, wrapper);

        return PageResult.build(resultPage.getRecords(), resultPage.getTotal(),
                resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBanner(BannerAddDTO bannerAddDTO) {
        BannerConfig banner = new BannerConfig();
        banner.setTitle(bannerAddDTO.getTitle());
        banner.setImageUrl(bannerAddDTO.getImageUrl());
        banner.setVideoUrl(bannerAddDTO.getVideoUrl());
        banner.setBannerType(bannerAddDTO.getBannerType() != null ? bannerAddDTO.getBannerType() : 1); // 默认图片类型
        banner.setLinkType(bannerAddDTO.getLinkType());
        banner.setLinkUrl(bannerAddDTO.getLinkValue());
        banner.setSort(bannerAddDTO.getSort());
        banner.setStatus(1); // 默认启用
        bannerConfigMapper.insert(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBanner(BannerUpdateDTO bannerUpdateDTO) {
        BannerConfig banner = bannerConfigMapper.selectById(bannerUpdateDTO.getId());
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        banner.setTitle(bannerUpdateDTO.getTitle());
        banner.setImageUrl(bannerUpdateDTO.getImageUrl());
        banner.setVideoUrl(bannerUpdateDTO.getVideoUrl());
        banner.setBannerType(bannerUpdateDTO.getBannerType());
        banner.setLinkType(bannerUpdateDTO.getLinkType());
        banner.setLinkUrl(bannerUpdateDTO.getLinkValue());
        banner.setSort(bannerUpdateDTO.getSort());
        banner.setStatus(bannerUpdateDTO.getStatus());
        bannerConfigMapper.updateById(banner);
    }

    @Override
    public List<BannerConfig> getBannerList() {
        return bannerConfigMapper.selectList(
                new LambdaQueryWrapper<BannerConfig>()
                        .orderByAsc(BannerConfig::getSort)
                        .orderByDesc(BannerConfig::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigUpdateDTO configUpdateDTO) {
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>()
                        .eq(SystemConfig::getConfigKey, configUpdateDTO.getConfigKey())
        );

        if (config == null) {
            // 不存在则创建
            config = new SystemConfig();
            config.setConfigKey(configUpdateDTO.getConfigKey());
            config.setConfigValue(configUpdateDTO.getConfigValue());
            systemConfigMapper.insert(config);
        } else {
            config.setConfigValue(configUpdateDTO.getConfigValue());
            systemConfigMapper.updateById(config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigBatch(Map<String, String> configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = String.valueOf(entry.getValue());

            SystemConfig config = systemConfigMapper.selectOne(
                    new LambdaQueryWrapper<SystemConfig>()
                            .eq(SystemConfig::getConfigKey, configKey)
            );

            if (config == null) {
                // 不存在则创建
                config = new SystemConfig();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                systemConfigMapper.insert(config);
            } else {
                config.setConfigValue(configValue);
                systemConfigMapper.updateById(config);
            }
        }
    }

    @Override
    public List<SystemConfig> getConfigList() {
        return systemConfigMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<IntegralCard> batchGenerateIntegralCard(IntegralCardBatchDTO batchDTO) {
        // 参数校验
        if (batchDTO.getCount() == null || batchDTO.getCount() <= 0) {
            throw new BusinessException("生成数量必须大于0");
        }

        // 设置默认积分额度为100
        Integer integralAmount = batchDTO.getIntegralAmount();
        if (integralAmount == null || integralAmount <= 0) {
            integralAmount = 100;
        }

        List<IntegralCard> cardList = new ArrayList<>();
        String batchNo = "BATCH_" + System.currentTimeMillis();

        LocalDateTime expireTime = null;
        if (batchDTO.getExpireDays() != null && batchDTO.getExpireDays() > 0) {
            expireTime = LocalDateTime.now().plusDays(batchDTO.getExpireDays());
        }

        for (int i = 0; i < batchDTO.getCount(); i++) {
            IntegralCard card = new IntegralCard();

            // 生成唯一的卡号（16位字母数字组合）
            String cardNo = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER.toUpperCase(), 16);
            // 生成唯一的兑换码（16位字母数字组合）
            String cardCode = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER.toUpperCase(), 16);

            card.setCardNo(cardNo);
            card.setCardCode(cardCode);
            card.setIntegralAmount(integralAmount);
            card.setCardStatus(0); // 0-未使用
            card.setExpireTime(expireTime);
            card.setBatchNo(batchNo);

            integralCardMapper.insert(card);
            cardList.add(card);

            // 避免卡号重复，每次生成后暂停1毫秒
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error("生成积分卡时发生异常", e);
            }
        }

        return cardList;
    }

    @Override
    public PageResult<IntegralCardVO> getIntegralCardList(Integer page, Integer pageSize, String keyword, Integer status) {
        Page<IntegralCard> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<IntegralCard> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(IntegralCard::getCardNo, keyword)
                    .or()
                    .like(IntegralCard::getCardCode, keyword)
                    .or()
                    .like(IntegralCard::getBatchNo, keyword));
        }

        if (status != null) {
            wrapper.eq(IntegralCard::getCardStatus, status);
        }

        wrapper.orderByDesc(IntegralCard::getCreateTime);
        Page<IntegralCard> resultPage = integralCardMapper.selectPage(pageParam, wrapper);

        // 转换为VO并填充用户信息
        List<IntegralCardVO> voList = resultPage.getRecords().stream().map(card -> {
            IntegralCardVO vo = new IntegralCardVO();
            vo.setId(card.getId());
            vo.setCardNo(card.getCardNo());
            vo.setCardCode(card.getCardCode());
            vo.setIntegralAmount(card.getIntegralAmount());
            vo.setCardStatus(card.getCardStatus());
            vo.setUseUserId(card.getUseUserId());
            vo.setUseTime(card.getUseTime());
            vo.setExpireTime(card.getExpireTime());
            vo.setBatchNo(card.getBatchNo());
            vo.setCreateTime(card.getCreateTime());
            vo.setUpdateTime(card.getUpdateTime());
            vo.setIsDeleted(card.getIsDeleted());

            // 如果已使用，查询用户信息
            if (card.getUseUserId() != null) {
                UserInfo user = userInfoMapper.selectById(card.getUseUserId());
                if (user != null) {
                    vo.setUsername(user.getNickname());
                }
            }

            return vo;
        }).collect(java.util.stream.Collectors.toList());

        return PageResult.build(voList, resultPage.getTotal(),
                resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBanner(Long id) {
        BannerConfig banner = bannerConfigMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        bannerConfigMapper.deleteById(id);
    }

    @Override
    public PageResult<UserInfo> getAgentList(Integer page, Integer pageSize) {
        Page<UserInfo> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();

        // 查询代理用户（通过 agentLevel 判断）
        // agentLevel = 1: 一级代理
        // agentLevel = 2: 二级代理
        wrapper.isNotNull(UserInfo::getAgentLevel);
        wrapper.gt(UserInfo::getAgentLevel, 0);
        wrapper.orderByDesc(UserInfo::getCreateTime);

        Page<UserInfo> resultPage = userInfoMapper.selectPage(pageParam, wrapper);
        return PageResult.build(resultPage.getRecords(), resultPage.getTotal(),
                resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public List<OrderInfo> getRecentOrders(Integer limit) {
        Page<OrderInfo> pageParam = new Page<>(1, limit);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OrderInfo::getCreateTime);

        Page<OrderInfo> resultPage = orderInfoMapper.selectPage(pageParam, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        // 统计用户总数
        Long totalUsers = userInfoMapper.selectCount(null);

        // 统计订单总数
        Long totalOrders = orderInfoMapper.selectCount(null);

        // 统计总收入（已完成订单）
        LambdaQueryWrapper<OrderInfo> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OrderInfo::getOrderStatus, 3); // 3-已完成
        List<OrderInfo> completedOrders = orderInfoMapper.selectList(orderWrapper);
        BigDecimal totalRevenue = completedOrders.stream()
                .map(OrderInfo::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计商品总数
        Long totalProducts = productInfoMapper.selectCount(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalProducts", totalProducts);

        return stats;
    }

    // ==================== 商品规格管理 ====================

    @Override
    public List<ProductSpecDTO> getProductSpecs(Long productId) {
        LambdaQueryWrapper<ProductSpec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSpec::getProductId, productId);
        wrapper.orderByAsc(ProductSpec::getSortOrder);

        List<ProductSpec> specs = productSpecMapper.selectList(wrapper);
        List<ProductSpecDTO> result = new ArrayList<>();
        for (ProductSpec spec : specs) {
            ProductSpecDTO dto = BeanUtil.copyProperties(spec, ProductSpecDTO.class);
            // 计算VIP价格
            calculateSpecVipPrice(dto);
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductSpecDTO addProductSpec(ProductSpecAddDTO addDTO) {
        // 检查商品是否存在
        ProductInfo product = productInfoMapper.selectById(addDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductSpec spec = BeanUtil.copyProperties(addDTO, ProductSpec.class);
        spec.setStatus(1); // 默认启用
        productSpecMapper.insert(spec);

        return BeanUtil.copyProperties(spec, ProductSpecDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductSpecDTO updateProductSpec(Long specId, ProductSpecUpdateDTO updateDTO) {
        ProductSpec spec = productSpecMapper.selectById(specId);
        if (spec == null) {
            throw new BusinessException("规格不存在");
        }

        // 更新规格信息
        if (updateDTO.getSpecName() != null) {
            spec.setSpecName(updateDTO.getSpecName());
        }
        if (updateDTO.getPrice() != null) {
            spec.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getVipPrice() != null) {
            spec.setVipPrice(updateDTO.getVipPrice());
        }
        if (updateDTO.getStock() != null) {
            spec.setStock(updateDTO.getStock());
        }
        if (updateDTO.getSortOrder() != null) {
            spec.setSortOrder(updateDTO.getSortOrder());
        }
        if (updateDTO.getStatus() != null) {
            spec.setStatus(updateDTO.getStatus());
        }

        productSpecMapper.updateById(spec);
        return BeanUtil.copyProperties(spec, ProductSpecDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductSpec(Long specId) {
        ProductSpec spec = productSpecMapper.selectById(specId);
        if (spec == null) {
            throw new BusinessException("规格不存在");
        }
        productSpecMapper.deleteById(specId);
    }
}
