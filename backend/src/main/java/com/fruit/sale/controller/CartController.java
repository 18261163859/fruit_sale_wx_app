package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.dto.CartAddDTO;
import com.fruit.sale.dto.CartUpdateDTO;
import com.fruit.sale.service.ICartService;
import com.fruit.sale.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "购物车管理", description = "购物车相关接口")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @Operation(summary = "添加商品到购物车", description = "添加商品到购物车")
    @PostMapping("/add")
    public Result<String> addCart(HttpServletRequest request, @RequestBody CartAddDTO cartAddDTO) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addCart(userId, cartAddDTO);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新购物车商品数量", description = "更新购物车中商品的数量")
    @PutMapping("/update/{cartId}")
    public Result<String> updateCart(HttpServletRequest request,
                                     @PathVariable Long cartId,
                                     @RequestBody CartUpdateDTO cartUpdateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateCart(userId, cartId, cartUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除购物车商品", description = "从购物车中删除商品")
    @DeleteMapping("/remove/{cartId}")
    public Result<String> removeCart(HttpServletRequest request, @PathVariable Long cartId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeCart(userId, cartId);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取购物车列表", description = "获取当前用户的购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartVO> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }

    @Operation(summary = "选中/取消选中商品", description = "设置购物车商品的选中状态")
    @PutMapping("/select/{cartId}")
    public Result<String> selectCart(HttpServletRequest request,
                                     @PathVariable Long cartId,
                                     @RequestParam Integer isSelected) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.selectCart(userId, cartId, isSelected);
        return Result.success("操作成功");
    }

    @Operation(summary = "清空购物车", description = "清空当前用户的购物车")
    @DeleteMapping("/clear")
    public Result<String> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success("清空成功");
    }
}