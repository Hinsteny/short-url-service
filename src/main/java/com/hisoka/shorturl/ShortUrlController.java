package com.hisoka.shorturl;

import com.alibaba.fastjson.JSON;
import com.hisoka.infrastructure.enums.ApiResultCodeType;
import com.hisoka.infrastructure.response.BaseResponse;
import com.hisoka.infrastructure.util.ResponseUtil;
import com.hisoka.shorturl.request.ShortUrlRequest;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user http-service interface
 * @author Hinsteny
 * @version $ID: UserController 2019-03-28 20:13 All rights reserved.$
 */
@RestController
@RequestMapping("/short")
public class ShortUrlController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private ShortUrlService shortUrlService;

    @GetMapping("")
    public BaseResponse query(String shortId) {
        logger.info("获取短链接所对应的长链接: {}", shortId);
        if (StringUtils.isEmpty(shortId)) {
            return ResponseUtil.failureResponse(ApiResultCodeType.PARAMETER_ILLEGAL, "短连接内容无效");
        }
        try {
            return ResponseUtil.successResponse(shortUrlService.getSourceUrl(shortId));
        } catch (Exception e) {
            logger.error("短连接查询异常", e);
            return ResponseUtil.failureResponse(ApiResultCodeType.PARAMETER_ILLEGAL, e.getMessage());
        }
    }

    @PostMapping("")
    public BaseResponse create(@RequestBody @Valid ShortUrlRequest request) {
        logger.info("创建用户: {}", JSON.toJSONString(request));
        try {
            String shortUrl = shortUrlService.buildShortUrl(request);
            return ResponseUtil.successResponse(shortUrl);
        } catch (Exception e) {
            logger.error("生成短链接异常", e);
            return ResponseUtil.failureResponse(ApiResultCodeType.PARAMETER_ILLEGAL, e.getMessage());
        }
    }

}
