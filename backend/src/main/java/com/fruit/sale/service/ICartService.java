package com.fruit.sale.service;

import com.fruit.sale.dto.CartAddDTO;
import com.fruit.sale.dto.CartUpdateDTO;
import com.fruit.sale.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface ICartService {

    /**
     * 添加商品到购物车
     */
    void addCart(Long userId, CartAddDTO cartAddDTO);

    /**
     * 更新购物车商品数量
     */
    void updateCart(Long userId, Long cartId, CartUpdateDTO cartUpdateDTO);

    /**
     * 删除购物车商品
     */
    void removeCart(Long userId, Long cartId);

    /**
     * 获取购物车列表
     */
    List<CartVO> getCartList(Long userId);

    /**
     * 选中/取消选中商品
     */
    void selectCart(Long userId, Long cartId, Integer isSelected);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}