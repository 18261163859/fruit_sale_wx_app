package com.fruit.sale.service;

import com.fruit.sale.dto.ShareCreateDTO;
import com.fruit.sale.vo.ShareLinkVO;
import com.fruit.sale.vo.ShareRecordVO;

import java.util.List;

/**
 * 分享服务接口
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
public interface IShareService {

    /**
     * 创建分享链接
     */
    ShareLinkVO createShare(Long userId, ShareCreateDTO shareCreateDTO);

    /**
     * 获取分享记录
     */
    List<ShareRecordVO> getShareRecords(Long userId);
}