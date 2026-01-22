package com.fruit.sale.controller;

import com.fruit.sale.common.PageResult;
import com.fruit.sale.common.Result;
import com.fruit.sale.dto.*;
import com.fruit.sale.entity.*;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.UserInfoMapper;
import com.fruit.sale.service.IAdminService;
import com.fruit.sale.service.IProductService;
import com.fruit.sale.vo.IntegralCardVO;
import com.fruit.sale.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "后台管理", description = "后台管理相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IProductService productService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private com.fruit.sale.service.IAgentService agentService;

    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持关键词搜索")
    @GetMapping("/users")
    public Result<PageResult<UserInfo>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) Integer agentLevel,
                                                     @RequestParam(required = false) Integer vipLevel) {
        PageResult<UserInfo> result = adminService.getUserList(page, pageSize, keyword, agentLevel, vipLevel);
        return Result.success(result);
    }

    @Operation(summary = "设置用户为代理", description = "将用户设置为一级或二级代理")
    @PutMapping("/user/set-agent")
    public Result<String> setUserAgent(@RequestBody SetAgentDTO setAgentDTO) {
        adminService.setUserAgent(setAgentDTO);
        return Result.success("设置成功");
    }

    @Operation(summary = "设置用户为一级代理", description = "快速将用户设置为一级代理")
    @PostMapping("/users/{userId}/set-agent")
    public Result<String> setUserAsFirstAgent(@PathVariable Long userId) {
        SetAgentDTO setAgentDTO = new SetAgentDTO();
        setAgentDTO.setUserId(userId);
        setAgentDTO.setAgentLevel(2); // 2对应一级代理(userType=3)
        setAgentDTO.setCommissionRate(new java.math.BigDecimal("0.10")); // 默认10%返现
        adminService.setUserAgent(setAgentDTO);
        return Result.success("设置成功");
    }

    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/users/{userId}/status")
    public Result<String> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            throw new BusinessException("状态参数不能为空");
        }
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userInfoMapper.updateById(user);
        return Result.success("更新成功");
    }

    @Operation(summary = "设置发货人员", description = "设置或取消用户的发货人员身份")
    @PutMapping("/users/{userId}/shipper")
    public Result<String> setUserShipper(@PathVariable Long userId, @RequestBody Map<String, Integer> params) {
        Integer isShipper = params.get("isShipper");
        if (isShipper == null) {
            throw new BusinessException("发货人员标识参数不能为空");
        }
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setIsShipper(isShipper);
        userInfoMapper.updateById(user);
        return Result.success(isShipper == 1 ? "已设置为发货人员" : "已取消发货人员");
    }

    @Operation(summary = "更新用户信息", description = "更新用户所有信息")
    @PutMapping("/users/{userId}")
    public Result<String> updateUser(@PathVariable Long userId, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新基本信息
        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        if (userUpdateDTO.getUserType() != null) {
            user.setUserType(userUpdateDTO.getUserType());
        }
        if (userUpdateDTO.getAgentLevel() != null) {
            user.setAgentLevel(userUpdateDTO.getAgentLevel());
        }
        if (userUpdateDTO.getParentAgentId() != null) {
            user.setParentAgentId(userUpdateDTO.getParentAgentId());
        }
        if (userUpdateDTO.getCommissionRate() != null) {
            user.setCommissionRate(userUpdateDTO.getCommissionRate());
        }
        if (userUpdateDTO.getIntegralBalance() != null) {
            user.setIntegralBalance(userUpdateDTO.getIntegralBalance());
        }
        // VIP到期时间允许设置为null来清除VIP状态
        user.setVipExpireTime(userUpdateDTO.getVipExpireTime());
        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(userUpdateDTO.getStatus());
        }
        if (userUpdateDTO.getIsShipper() != null) {
            user.setIsShipper(userUpdateDTO.getIsShipper());
        }

        // 使用 UpdateWrapper 强制更新所有字段（包括 null 值）
        userInfoMapper.update(user, new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<UserInfo>()
                .eq("id", user.getId())
                .eq("is_deleted", 0));
        return Result.success("更新成功");
    }

    @Operation(summary = "获取代理申请列表", description = "分页获取代理申请列表")
    @GetMapping("/agent-apply/list")
    public Result<PageResult<AgentApply>> getAgentApplyList(@RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                                             @RequestParam(required = false) Integer status) {
        PageResult<AgentApply> result = adminService.getAgentApplyList(page, pageSize, status);
        return Result.success(result);
    }

    @Operation(summary = "审批代理申请", description = "审批通过或拒绝代理申请")
    @PutMapping("/agent-apply/audit")
    public Result<String> auditAgentApply(HttpServletRequest request, @RequestBody AgentApplyAuditDTO auditDTO) {
        Long adminId = (Long) request.getAttribute("userId");
        adminService.auditAgentApply(adminId, auditDTO);
        return Result.success("审批成功");
    }

    @Operation(summary = "添加商品", description = "添加新的商品")
    @PostMapping("/product/add")
    public Result<String> addProduct(@RequestBody ProductAddDTO productAddDTO) {
        adminService.addProduct(productAddDTO);
        return Result.success("添加成功");
    }

    @Operation(summary = "创建商品（RESTful）", description = "创建新的商品")
    @PostMapping("/products")
    public Result<String> createProduct(@RequestBody ProductAddDTO productAddDTO) {
        adminService.addProduct(productAddDTO);
        return Result.success("创建成功");
    }

    @Operation(summary = "获取商品详情（RESTful）", description = "获取指定商品的详细信息")
    @GetMapping("/products/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id) {
        ProductVO product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @Operation(summary = "更新商品", description = "更新商品信息")
    @PutMapping("/product/update")
    public Result<String> updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO) {
        adminService.updateProduct(productUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "更新商品（RESTful）", description = "更新商品信息")
    @PutMapping("/products/{id}")
    public Result<String> updateProductById(@PathVariable Long id, @RequestBody ProductUpdateDTO productUpdateDTO) {
        productUpdateDTO.setId(id);
        adminService.updateProduct(productUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除商品（RESTful）", description = "删除指定商品")
    @DeleteMapping("/products/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "上下架商品", description = "设置商品上架或下架状态")
    @PutMapping("/product/status")
    public Result<String> updateProductStatus(@RequestParam Long productId, @RequestParam Integer status) {
        adminService.updateProductStatus(productId, status);
        return Result.success("操作成功");
    }

    @Operation(summary = "更新商品状态（RESTful）", description = "更新商品上架或下架状态")
    @PutMapping("/products/{id}/status")
    public Result<String> updateProductStatusById(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            throw new BusinessException("状态参数不能为空");
        }
        adminService.updateProductStatus(id, status);
        return Result.success("操作成功");
    }

    @Operation(summary = "设置商品推荐", description = "设置商品是否推荐")
    @PutMapping("/products/{id}/recommend")
    public Result<String> updateProductRecommend(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer isRecommend = params.get("isRecommend");
        if (isRecommend == null) {
            throw new BusinessException("推荐状态参数不能为空");
        }
        productService.updateRecommend(id, isRecommend);
        return Result.success("操作成功");
    }

    @Operation(summary = "修改商品销量", description = "手动修改商品的销量")
    @PutMapping("/products/{id}/sales")
    public Result<String> updateProductSales(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer salesCount = params.get("salesCount");
        if (salesCount == null) {
            throw new BusinessException("销量参数不能为空");
        }
        if (salesCount < 0) {
            throw new BusinessException("销量不能为负数");
        }
        adminService.updateProductSales(id, salesCount);
        return Result.success("操作成功");
    }

    @Operation(summary = "获取所有订单", description = "分页获取订单列表")
    @GetMapping("/orders")
    public Result<PageResult<com.fruit.sale.vo.OrderVO>> getOrderList(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(required = false) Integer status) {
        PageResult<OrderInfo> result = adminService.getOrderList(page, pageSize, status);

        // 转换为OrderVO
        List<com.fruit.sale.vo.OrderVO> voList = result.getRecords().stream()
                .map(order -> orderService.getOrderDetail(order.getId()))
                .collect(java.util.stream.Collectors.toList());

        PageResult<com.fruit.sale.vo.OrderVO> voResult = new PageResult<>(
                voList, result.getTotal(), result.getCurrent(), result.getSize());

        return Result.success(voResult);
    }

    @Autowired
    private com.fruit.sale.service.IOrderService orderService;

    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @GetMapping("/orders/{orderId}")
    public Result<com.fruit.sale.vo.OrderVO> getOrderDetail(@PathVariable Long orderId) {
        com.fruit.sale.vo.OrderVO order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    @Operation(summary = "发货", description = "订单发货并填写物流信息")
    @PostMapping("/orders/{id}/ship")
    public Result<String> shipOrder(@PathVariable Long id, @RequestBody ShipOrderDTO shipOrderDTO) {
        shipOrderDTO.setOrderId(id);
        orderService.shipOrder(shipOrderDTO);
        return Result.success("发货成功");
    }

    @Operation(summary = "确认完成订单", description = "确认订单完成")
    @PostMapping("/orders/{id}/finish")
    public Result<String> finishOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success("操作成功");
    }

    @Operation(summary = "添加轮播图", description = "添加新的轮播图")
    @PostMapping("/banner/add")
    public Result<String> addBanner(@RequestBody BannerAddDTO bannerAddDTO) {
        adminService.addBanner(bannerAddDTO);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新轮播图", description = "更新轮播图信息")
    @PutMapping("/banner/update")
    public Result<String> updateBanner(@RequestBody BannerUpdateDTO bannerUpdateDTO) {
        adminService.updateBanner(bannerUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "获取轮播图列表", description = "获取所有轮播图")
    @GetMapping("/banner/list")
    public Result<List<BannerConfig>> getBannerList() {
        List<BannerConfig> banners = adminService.getBannerList();
        return Result.success(banners);
    }

    @Operation(summary = "更新系统配置", description = "批量更新系统配置项")
    @PutMapping("/config/update")
    public Result<String> updateConfig(@RequestBody Map<String, String> configMap) {
        adminService.updateConfigBatch(configMap);
        return Result.success("更新成功");
    }

    @Operation(summary = "获取系统配置", description = "获取所有系统配置")
    @GetMapping("/config/list")
    public Result<List<SystemConfig>> getConfigList() {
        List<SystemConfig> configs = adminService.getConfigList();
        return Result.success(configs);
    }

    @Operation(summary = "批量生成积分卡", description = "批量生成积分兑换卡")
    @PostMapping("/integral-card/batch")
    public Result<List<IntegralCard>> batchGenerateIntegralCard(@RequestBody IntegralCardBatchDTO batchDTO) {
        List<IntegralCard> cards = adminService.batchGenerateIntegralCard(batchDTO);
        return Result.success(cards);
    }

    // ===== 积分卡管理（新增接口，匹配前端） =====
    @Operation(summary = "获取积分卡列表", description = "分页获取积分卡列表")
    @GetMapping("/point-cards")
    public Result<PageResult<IntegralCardVO>> getPointCardList(@RequestParam(defaultValue = "1") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(required = false) Integer status) {
        PageResult<IntegralCardVO> result = adminService.getIntegralCardList(page, pageSize, keyword, status);
        return Result.success(result);
    }

    @Operation(summary = "生成积分卡", description = "生成指定数量和面值的积分卡")
    @PostMapping("/point-cards/generate")
    public Result<String> generatePointCards(@RequestBody IntegralCardBatchDTO batchDTO) {
        adminService.batchGenerateIntegralCard(batchDTO);
        return Result.success("生成成功");
    }

    // ===== 商品管理（新增接口） =====
    @Operation(summary = "获取商品列表", description = "分页获取商品列表")
    @GetMapping("/products")
    public Result<PageResult<ProductInfo>> getProductList(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Integer categoryId,
                                                       @RequestParam(required = false) Integer status) {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setCurrent(page.longValue());
        queryDTO.setSize(pageSize.longValue());
        queryDTO.setProductName(keyword);
        if (categoryId != null) {
            queryDTO.setCategoryId(categoryId.longValue());
        }
        if (status != null) {
            queryDTO.setStatus(status);
        }

        PageResult<?> voResult = productService.pageQuery(queryDTO);
        // For admin, return the result with actual records
        @SuppressWarnings("unchecked")
        PageResult<ProductInfo> result = new PageResult<>(
            (List<ProductInfo>) voResult.getRecords(),
            voResult.getTotal(),
            voResult.getCurrent(),
            voResult.getSize()
        );
        return Result.success(result);
    }

    // ===== 商品规格管理 =====
    @Operation(summary = "获取商品规格列表", description = "获取指定商品的所有规格")
    @GetMapping("/products/{productId}/specs")
    public Result<List<ProductSpecDTO>> getProductSpecs(@PathVariable Long productId) {
        List<ProductSpecDTO> specs = adminService.getProductSpecs(productId);
        return Result.success(specs);
    }

    @Operation(summary = "添加商品规格", description = "为商品添加新的规格")
    @PostMapping("/products/{productId}/specs")
    public Result<ProductSpecDTO> addProductSpec(@PathVariable Long productId, @RequestBody ProductSpecAddDTO addDTO) {
        addDTO.setProductId(productId);
        ProductSpecDTO spec = adminService.addProductSpec(addDTO);
        return Result.success(spec);
    }

    @Operation(summary = "更新商品规格", description = "更新指定商品规格")
    @PutMapping("/products/specs/{specId}")
    public Result<ProductSpecDTO> updateProductSpec(@PathVariable Long specId, @RequestBody ProductSpecUpdateDTO updateDTO) {
        ProductSpecDTO spec = adminService.updateProductSpec(specId, updateDTO);
        return Result.success(spec);
    }

    @Operation(summary = "删除商品规格", description = "删除指定商品规格")
    @DeleteMapping("/products/specs/{specId}")
    public Result<String> deleteProductSpec(@PathVariable Long specId) {
        adminService.deleteProductSpec(specId);
        return Result.success("删除成功");
    }

    // ===== 分类管理（新增接口） =====
    @Operation(summary = "获取分类列表", description = "获取所有分类")
    @GetMapping("/categories")
    public Result<List<ProductCategory>> getCategoryList() {
        List<ProductCategory> categories = productService.getAllCategories();
        return Result.success(categories);
    }

    @Operation(summary = "创建分类", description = "创建新的分类")
    @PostMapping("/categories")
    public Result<String> createCategory(@RequestBody ProductCategory category) {
        productService.createCategory(category);
        return Result.success("创建成功");
    }

    @Operation(summary = "更新分类", description = "更新分类信息")
    @PutMapping("/categories/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        productService.updateCategory(category);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除分类", description = "删除指定分类")
    @DeleteMapping("/categories/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        productService.deleteCategory(id);
        return Result.success("删除成功");
    }

    // ===== 轮播图管理（新增RESTful接口） =====
    @Operation(summary = "获取轮播图列表", description = "获取所有轮播图")
    @GetMapping("/banners")
    public Result<List<BannerConfig>> getBanners() {
        return Result.success(adminService.getBannerList());
    }

    @Operation(summary = "创建轮播图", description = "创建新的轮播图")
    @PostMapping("/banners")
    public Result<String> createBanner(@RequestBody BannerAddDTO bannerAddDTO) {
        adminService.addBanner(bannerAddDTO);
        return Result.success("创建成功");
    }

    @Operation(summary = "更新轮播图", description = "更新轮播图信息")
    @PutMapping("/banners/{id}")
    public Result<String> updateBannerById(@PathVariable Long id, @RequestBody BannerUpdateDTO bannerUpdateDTO) {
        bannerUpdateDTO.setId(id);
        adminService.updateBanner(bannerUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除轮播图", description = "删除指定轮播图")
    @DeleteMapping("/banners/{id}")
    public Result<String> deleteBanner(@PathVariable Long id) {
        adminService.deleteBanner(id);
        return Result.success("删除成功");
    }

    // ===== 代理管理（新增接口） =====
    @Operation(summary = "代理列表", description = "获取代理用户列表")
    @GetMapping("/agents")
    public Result<PageResult<UserInfo>> getAgentList(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<UserInfo> result = adminService.getAgentList(page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "代理申请列表", description = "获取代理申请列表")
    @GetMapping("/agent/applications")
    public Result<PageResult<AgentApply>> getAgentApplications(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getAgentApplyList(page, pageSize, status));
    }

    // ===== 系统配置（新增接口） =====
    @Autowired
    private com.fruit.sale.service.ISystemConfigService systemConfigService;

    @Autowired
    private com.fruit.sale.service.ICommissionService commissionService;

    @Operation(summary = "获取新年主题配置", description = "获取新年主题配置")
    @GetMapping("/config/newyear-theme")
    public Result<Map<String, Object>> getNewYearThemeConfig() {
        Map<String, Object> config = systemConfigService.getThemeConfig();
        return Result.success(config);
    }

    @Operation(summary = "设置新年主题配置", description = "设置新年主题配置（可针对普通用户和VIP用户分别设置）")
    @PutMapping("/config/newyear-theme")
    public Result<String> setNewYearThemeConfig(@RequestBody com.fruit.sale.dto.NewYearThemeConfigDTO configDTO) {
        systemConfigService.setNewYearThemeConfig(
            configDTO.getNormalUserEnabled(),
            configDTO.getVipUserEnabled()
        );
        return Result.success("设置成功");
    }

    // ===== 返现管理（新增接口） =====
    @Operation(summary = "获取返现记录列表", description = "分页获取返现记录列表")
    @GetMapping("/commission/records")
    public Result<PageResult<com.fruit.sale.vo.CommissionRecordVO>> getCommissionRecords(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long agentId) {
        PageResult<com.fruit.sale.vo.CommissionRecordVO> result = commissionService.getCommissionRecords(agentId != null ? agentId : 0L, current, size);
        return Result.success(result);
    }

    @Operation(summary = "获取返现统计", description = "获取全局返现统计信息")
    @GetMapping("/commission/statistics")
    public Result<Map<String, Object>> getCommissionStatistics() {
        // 这里可以添加全局统计逻辑
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalCommission", 0);
        stats.put("settledAmount", 0);
        stats.put("pendingAmount", 0);
        return Result.success(stats);
    }

    // ===== 代理邀请申请管理 =====
    @Operation(summary = "获取代理邀请申请列表", description = "获取所有或指定状态的代理邀请申请（邀请二级代理）")
    @GetMapping("/agent/invite-applications")
    public Result<List<com.fruit.sale.vo.AgentApplicationVO>> getAgentInviteApplications(
            @RequestParam(required = false) Integer status) {
        List<com.fruit.sale.vo.AgentApplicationVO> result = agentService.getApplicationList(status);
        return Result.success(result);
    }

    @Operation(summary = "审核代理邀请申请", description = "审核通过或拒绝代理邀请申请（邀请二级代理）")
    @PostMapping("/agent/invite-applications/review")
    public Result<String> reviewAgentInviteApplication(
            HttpServletRequest request,
            @RequestBody com.fruit.sale.dto.ReviewAgentApplicationDTO dto) {
        // 从请求中获取管理员ID
        Long adminId = (Long) request.getAttribute("userId");
        if (adminId == null) {
            throw new BusinessException("未授权");
        }

        agentService.reviewApplication(adminId, dto);
        return Result.success("审核成功");
    }

    // ===== 返现申请管理 =====
    @Operation(summary = "获取返现申请列表", description = "获取所有或指定状态的返现申请")
    @GetMapping("/commission-applications")
    public Result<List<com.fruit.sale.vo.CommissionApplicationVO>> getCommissionApplications(
            @RequestParam(required = false) Integer status) {
        List<com.fruit.sale.vo.CommissionApplicationVO> result = agentService.getCommissionApplicationList(status);
        return Result.success(result);
    }

    @Operation(summary = "审核返现申请", description = "审核通过或拒绝返现申请")
    @PostMapping("/commission-applications/review")
    public Result<String> reviewCommissionApplication(
            HttpServletRequest request,
            @RequestBody com.fruit.sale.dto.ReviewCommissionApplicationDTO dto) {
        Long reviewerId = (Long) request.getAttribute("userId");
        if (reviewerId == null) {
            throw new BusinessException("未授权");
        }

        agentService.reviewCommissionApplication(reviewerId, dto);
        return Result.success("审核成功");
    }

    @Operation(summary = "标记返现申请为已返现", description = "将审核通过的返现申请标记为已返现")
    @PostMapping("/commission-applications/{id}/transfer")
    public Result<String> markAsTransferred(
            HttpServletRequest request,
            @PathVariable("id") Long applicationId) {
        Long adminId = (Long) request.getAttribute("userId");
        if (adminId == null) {
            throw new BusinessException("未授权");
        }

        agentService.markAsTransferred(adminId, applicationId);
        return Result.success("已标记为已返现");
    }
}