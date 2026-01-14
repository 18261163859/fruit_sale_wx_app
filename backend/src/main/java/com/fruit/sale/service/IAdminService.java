package com.fruit.sale.service;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.dto.*;
import com.fruit.sale.entity.*;
import com.fruit.sale.vo.IntegralCardVO;
import com.fruit.sale.vo.UserInfoVO;

import java.util.List;
import java.util.Map;

/**
 * 后台管理服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IAdminService {

    /**
     * 获取用户列表（分页）
     */
    PageResult<UserInfo> getUserList(Integer page, Integer pageSize, String keyword, Integer agentLevel, Integer vipLevel);

    /**
     * 设置用户为代理
     */
    void setUserAgent(SetAgentDTO setAgentDTO);

    /**
     * 获取代理申请列表
     */
    PageResult<AgentApply> getAgentApplyList(Integer page, Integer pageSize, Integer status);

    /**
     * 审批代理申请
     */
    void auditAgentApply(Long adminId, AgentApplyAuditDTO auditDTO);

    /**
     * 添加商品
     */
    void addProduct(ProductAddDTO productAddDTO);

    /**
     * 更新商品
     */
    void updateProduct(ProductUpdateDTO productUpdateDTO);

    /**
     * 上下架商品
     */
    void updateProductStatus(Long productId, Integer status);

    /**
     * 修改商品销量
     */
    void updateProductSales(Long productId, Integer salesCount);

    /**
     * 删除商品
     */
    void deleteProduct(Long productId);

    /**
     * 获取所有订单（分页）
     */
    PageResult<OrderInfo> getOrderList(Integer page, Integer pageSize, Integer status);

    /**
     * 添加轮播图
     */
    void addBanner(BannerAddDTO bannerAddDTO);

    /**
     * 更新轮播图
     */
    void updateBanner(BannerUpdateDTO bannerUpdateDTO);

    /**
     * 获取轮播图列表
     */
    List<BannerConfig> getBannerList();

    /**
     * 更新系统配置
     */
    void updateConfig(ConfigUpdateDTO configUpdateDTO);

    /**
     * 批量更新系统配置
     */
    void updateConfigBatch(Map<String, String> configMap);

    /**
     * 获取系统配置列表
     */
    List<SystemConfig> getConfigList();

    /**
     * 批量生成积分卡
     */
    List<IntegralCard> batchGenerateIntegralCard(IntegralCardBatchDTO batchDTO);

    /**
     * 获取积分卡列表（分页）
     */
    PageResult<IntegralCardVO> getIntegralCardList(Integer page, Integer pageSize, String keyword, Integer status);

    /**
     * 删除轮播图
     */
    void deleteBanner(Long id);

    /**
     * 获取代理列表
     */
    PageResult<UserInfo> getAgentList(Integer page, Integer pageSize);

    /**
     * 获取最近订单
     */
    List<OrderInfo> getRecentOrders(Integer limit);

    /**
     * 获取仪表盘统计数据
     */
    Map<String, Object> getDashboardStatistics();

    // ==================== 商品规格管理 ====================

    /**
     * 获取商品规格列表
     */
    List<ProductSpecDTO> getProductSpecs(Long productId);

    /**
     * 添加商品规格
     */
    ProductSpecDTO addProductSpec(ProductSpecAddDTO addDTO);

    /**
     * 更新商品规格
     */
    ProductSpecDTO updateProductSpec(Long specId, ProductSpecUpdateDTO updateDTO);

    /**
     * 删除商品规格
     */
    void deleteProductSpec(Long specId);
}