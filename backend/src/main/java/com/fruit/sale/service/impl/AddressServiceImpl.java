package com.fruit.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fruit.sale.dto.AddressAddDTO;
import com.fruit.sale.dto.AddressUpdateDTO;
import com.fruit.sale.entity.UserAddress;
import com.fruit.sale.exception.BusinessException;
import com.fruit.sale.mapper.UserAddressMapper;
import com.fruit.sale.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务实现
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, AddressAddDTO addressAddDTO) {
        UserAddress address = BeanUtil.copyProperties(addressAddDTO, UserAddress.class);
        address.setUserId(userId);

        // 如果设置为默认地址，需要先取消其他默认地址
        if (addressAddDTO.getIsDefault() != null && addressAddDTO.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        userAddressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, AddressUpdateDTO addressUpdateDTO) {
        UserAddress address = userAddressMapper.selectById(addressUpdateDTO.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 如果设置为默认地址，需要先取消其他默认地址
        if (addressUpdateDTO.getIsDefault() != null && addressUpdateDTO.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        BeanUtil.copyProperties(addressUpdateDTO, address);
        userAddressMapper.updateById(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        userAddressMapper.deleteById(addressId);
    }

    @Override
    public List<UserAddress> getAddressList(Long userId) {
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreateTime)
        );
    }

    @Override
    public UserAddress getAddressDetail(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 取消其他默认地址
        clearDefaultAddress(userId);

        // 设置为默认
        address.setIsDefault(1);
        userAddressMapper.updateById(address);
    }

    /**
     * 清除默认地址
     */
    private void clearDefaultAddress(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .set(UserAddress::getIsDefault, 0);
        userAddressMapper.update(null, wrapper);
    }
}