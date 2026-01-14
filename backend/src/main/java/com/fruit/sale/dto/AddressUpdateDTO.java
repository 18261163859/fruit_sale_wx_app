package com.fruit.sale.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新地址DTO
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Data
public class AddressUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 地址标签
     */
    private String addressTag;

    /**
     * 是否设为默认（0-否 1-是）
     */
    private Integer isDefault;
}