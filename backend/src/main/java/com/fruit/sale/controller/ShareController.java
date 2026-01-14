package com.fruit.sale.controller;

import com.fruit.sale.common.Result;
import com.fruit.sale.dto.ShareCreateDTO;
import com.fruit.sale.service.IShareService;
import com.fruit.sale.vo.ShareLinkVO;
import com.fruit.sale.vo.ShareRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分享管理控制器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Tag(name = "分享管理", description = "分享相关接口")
@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private IShareService shareService;

    @Operation(summary = "创建分享链接", description = "星享会员和代理可创建商品分享链接")
    @PostMapping("/create")
    public Result<ShareLinkVO> createShare(HttpServletRequest request, @RequestBody ShareCreateDTO shareCreateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        ShareLinkVO shareLink = shareService.createShare(userId, shareCreateDTO);
        return Result.success(shareLink);
    }

    @Operation(summary = "获取分享记录", description = "获取我的分享记录列表")
    @GetMapping("/records")
    public Result<List<ShareRecordVO>> getShareRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ShareRecordVO> records = shareService.getShareRecords(userId);
        return Result.success(records);
    }
}