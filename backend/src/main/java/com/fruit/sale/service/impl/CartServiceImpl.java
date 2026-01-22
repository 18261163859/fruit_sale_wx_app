package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruit.sale.common.ResultCode;
import com.fruit.sale.dto.CartAddDTO;
import com.fruit.sale.dto.CartUpdateDTO;
import com.fruit.sale.entity.ProductInfo;
import com.fruit.sale.entity.ShoppingCart;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.ProductInfoMapper;
import com.fruit.sale.mapper.ShoppingCartMapper;
import com.fruit.sale.service.ICartService;
import com.fruit.sale.service.ISystemConfigService;
import com.fruit.sale.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private com.fruit.sale.mapper.ProductSpecMapper productSpecMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCart(Long userId, CartAddDTO cartAddDTO) {
        // 校验商品是否存在
        ProductInfo product = productInfoMapper.selectById(cartAddDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (product.getStatus() == 0) {
            throw new BusinessException("商品已下架");
        }

        // 如果指定了规格,校验规格库存;否则校验商品总库存
        int availableStock = product.getStock();
        if (cartAddDTO.getSpecId() != null) {
            com.fruit.sale.entity.ProductSpec spec = productSpecMapper.selectById(cartAddDTO.getSpecId());
            if (spec == null) {
                throw new BusinessException("商品规格不存在");
            }
            if (spec.getStatus() == 0) {
                throw new BusinessException("该规格已下架");
            }
            availableStock = spec.getStock();
        }

        if (availableStock < cartAddDTO.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }

        // 查询购物车中是否已存在该商品(同商品同规格视为同一项)
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .eq(ShoppingCart::getProductId, cartAddDTO.getProductId());

        // 规格ID匹配
        if (cartAddDTO.getSpecId() != null) {
            wrapper.eq(ShoppingCart::getSpecId, cartAddDTO.getSpecId());
        } else {
            wrapper.isNull(ShoppingCart::getSpecId);
        }

        ShoppingCart existCart = shoppingCartMapper.selectOne(wrapper);

        if (existCart != null) {
            // 已存在则累加数量
            int newQuantity = existCart.getQuantity() + cartAddDTO.getQuantity();
            if (newQuantity > availableStock) {
                throw new BusinessException("商品库存不足");
            }
            existCart.setQuantity(newQuantity);
            shoppingCartMapper.updateById(existCart);
        } else {
            // 不存在则新增
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(cartAddDTO.getProductId());
            cart.setSpecId(cartAddDTO.getSpecId());
            cart.setQuantity(cartAddDTO.getQuantity());
            cart.setSpecInfo(cartAddDTO.getSpecInfo());
            cart.setIsSelected(1);
            shoppingCartMapper.insert(cart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCart(Long userId, Long cartId, CartUpdateDTO cartUpdateDTO) {
        ShoppingCart cart = shoppingCartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        // 校验库存
        ProductInfo product = productInfoMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (cartUpdateDTO.getQuantity() > product.getStock()) {
            throw new BusinessException("商品库存不足");
        }

        cart.setQuantity(cartUpdateDTO.getQuantity());
        shoppingCartMapper.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCart(Long userId, Long cartId) {
        ShoppingCart cart = shoppingCartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        shoppingCartMapper.deleteById(cartId);
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<ShoppingCart> cartList = shoppingCartMapper.selectList(
                new LambdaQueryWrapper<ShoppingCart>()
                        .eq(ShoppingCart::getUserId, userId)
                        .orderByDesc(ShoppingCart::getCreateTime)
        );

        // 获取VIP折扣率
        BigDecimal vipDiscountRate = getVipDiscountRate();

        List<CartVO> result = new ArrayList<>();
        for (ShoppingCart cart : cartList) {
            ProductInfo product = productInfoMapper.selectById(cart.getProductId());
            if (product != null) {
                CartVO vo = new CartVO();
                vo.setId(cart.getId());
                vo.setProductId(product.getId());
                vo.setProductName(product.getProductName());
                vo.setMainImage(product.getMainImage());

                // 价格和库存根据规格设置
                BigDecimal price = product.getPrice();
                BigDecimal vipPrice = product.getPrice().multiply(vipDiscountRate);
                Integer stock = product.getStock();

                // 如果有规格,使用规格的价格和库存
                if (cart.getSpecId() != null) {
                    com.fruit.sale.entity.ProductSpec spec = productSpecMapper.selectById(cart.getSpecId());
                    if (spec != null) {
                        vo.setSpecId(spec.getId());
                        vo.setSpecName(spec.getSpecName());
                        price = spec.getPrice();
                        vipPrice = spec.getPrice().multiply(vipDiscountRate);
                        stock = spec.getStock();
                    }
                }

                vo.setPrice(price);
                vo.setVipPrice(vipPrice);
                vo.setStock(stock);
                vo.setQuantity(cart.getQuantity());
                vo.setSpecInfo(cart.getSpecInfo());
                vo.setIsSelected(cart.getIsSelected());
                vo.setStatus(product.getStatus());
                vo.setCreateTime(cart.getCreateTime());
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectCart(Long userId, Long cartId, Integer isSelected) {
        ShoppingCart cart = shoppingCartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        cart.setIsSelected(isSelected);
        shoppingCartMapper.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        shoppingCartMapper.delete(
                new LambdaQueryWrapper<ShoppingCart>()
                        .eq(ShoppingCart::getUserId, userId)
        );
    }
}