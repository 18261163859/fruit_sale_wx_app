package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.dto.AddressAddDTO;
import com.fruit.sale.dto.AddressUpdateDTO;
import com.fruit.sale.entity.UserAddress;
import com.fruit.sale.service.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "收货地址管理", description = "收货地址相关接口")
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @Operation(summary = "添加收货地址", description = "添加新的收货地址")
    @PostMapping("/add")
    public Result<String> addAddress(HttpServletRequest request, @RequestBody AddressAddDTO addressAddDTO) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.addAddress(userId, addressAddDTO);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新收货地址", description = "更新已有的收货地址")
    @PutMapping("/update")
    public Result<String> updateAddress(HttpServletRequest request, @RequestBody AddressUpdateDTO addressUpdateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.updateAddress(userId, addressUpdateDTO);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除收货地址", description = "删除指定的收货地址")
    @DeleteMapping("/delete/{addressId}")
    public Result<String> deleteAddress(HttpServletRequest request, @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.deleteAddress(userId, addressId);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取地址列表", description = "获取当前用户的所有收货地址")
    @GetMapping("/list")
    public Result<List<UserAddress>> getAddressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<UserAddress> addressList = addressService.getAddressList(userId);
        return Result.success(addressList);
    }

    @Operation(summary = "获取地址详情", description = "获取指定地址的详细信息")
    @GetMapping("/{addressId}")
    public Result<UserAddress> getAddressDetail(HttpServletRequest request, @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        UserAddress address = addressService.getAddressDetail(userId, addressId);
        return Result.success(address);
    }

    @Operation(summary = "设置默认地址", description = "将指定地址设置为默认地址")
    @PutMapping("/default/{addressId}")
    public Result<String> setDefaultAddress(HttpServletRequest request, @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefaultAddress(userId, addressId);
        return Result.success("设置成功");
    }
}