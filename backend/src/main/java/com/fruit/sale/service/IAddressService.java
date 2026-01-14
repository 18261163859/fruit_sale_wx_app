package com.fruit.sale.service;

import com.fruit.sale.dto.AddressAddDTO;
import com.fruit.sale.dto.AddressUpdateDTO;
import com.fruit.sale.entity.UserAddress;

import java.util.List;

/**
 * 地址服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IAddressService {

    /**
     * 添加收货地址
     */
    void addAddress(Long userId, AddressAddDTO addressAddDTO);

    /**
     * 更新收货地址
     */
    void updateAddress(Long userId, AddressUpdateDTO addressUpdateDTO);

    /**
     * 删除收货地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 获取地址列表
     */
    List<UserAddress> getAddressList(Long userId);

    /**
     * 获取地址详情
     */
    UserAddress getAddressDetail(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}