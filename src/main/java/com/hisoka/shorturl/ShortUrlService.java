package com.hisoka.shorturl;

import com.hisoka.infrastructure.util.MD5Utils;
import com.hisoka.mybatis.mapper.SShortUrlMapper;
import com.hisoka.mybatis.pojo.SShortUrl;
import com.hisoka.mybatis.pojo.SShortUrlExample;
import com.hisoka.shorturl.request.ShortUrlRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author Hinsteny
 * @version $ID: ShortUrlService 2019-03-28 20:01 All rights reserved.$
 */
@Service
public class ShortUrlService {

    /**
     * https://
     */
    public static final String HTTPS_HEAD = "https://";

    @Value("${app.short.url.domain}")
    private String shortUrlDomain;

    @Value("${app.custom.url.max}")
    private Integer maxRetryTimes;

    @Resource
    private SShortUrlMapper sShortUrlMapper;

    /**
     * 获取短链地址
     * @param request
     * @return
     * @throws Exception
     */
    public String buildShortUrl(ShortUrlRequest request) throws Exception {
        StringBuilder builder = new StringBuilder();
        String shortId = genShortId(request);
        if (request.getWithHttpHead() != 0) {
            builder.append(HTTPS_HEAD);
        }
        builder.append(shortUrlDomain);
        builder.append("/");
        builder.append(shortId);
        return builder.toString();
    }

    /**
     * 获取原链
     * @param shortId
     * @return
     * @throws Exception
     */
    public String getSourceUrl(String shortId) throws Exception {
        SShortUrlExample example = new SShortUrlExample();
        example.createCriteria().andShortIdEqualTo(shortId);
        List<SShortUrl> shortUrls = sShortUrlMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(shortUrls)) {
            return shortUrls.stream().findFirst().get().getSourceUrl();
        }
        throw new Exception("所查询短链接key无效");
    }

    /**
     * 生成短链ID
     * @param request
     * @return
     * @throws Exception
     */
    private String genShortId(ShortUrlRequest request) throws Exception {
        // 自定义KEY的，可以先不考虑 HASH匹配，即使存在HASH KEY，那也可以按照自定义key生成
        String urlHash = MD5Utils.sign(request.getUrl());
        if (!StringUtils.isEmpty(request.getCustomKey())) {
            SShortUrlExample example = new SShortUrlExample();
            example.createCriteria().andShortIdEqualTo(request.getCustomKey());
            List<SShortUrl> shortUrls = sShortUrlMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(shortUrls)) {
                Optional<SShortUrl> first = shortUrls.stream().filter(item -> request.getUrl().equals(item.getSourceUrl())).findFirst();
                if (first.isPresent()) {
                    return first.get().getShortId();
                } else {
                    throw new Exception("自定义key已被占用");
                }
            } else {
                String shortId = insertShortId(request, urlHash);
                if (shortId != null) {
                    return shortId;
                }
            }
        }
        SShortUrlExample example = new SShortUrlExample();
        example.createCriteria().andSourceUrlHashEqualTo(urlHash);
        List<SShortUrl> exists = sShortUrlMapper.selectByExample(example);
        // 通过URLhash来查询，这样索引就可以不用建立在URL上
        if (exists != null && !exists.isEmpty()) {
            Optional<SShortUrl> first = exists.stream().filter(item -> request.getUrl().equals(item.getSourceUrl())).findFirst();
            if (first.isPresent()) {
                return first.get().getShortId();
            }
            // hash碰撞，url又不一样, 多hash一次
            urlHash = MD5Utils.sign(urlHash + request.getUrl());
        }
        int tryTimes = maxRetryTimes;
        do {
            tryTimes--;
            String shortId = insertShortId(request, urlHash);
            if (shortId != null) {
                return shortId;
            }
        } while (tryTimes > 0);
        throw new Exception("系统错误, 请稍后重试");
    }

    /**
     * 往数据数插入新生成的shortId
     * @param request
     * @param urlHash
     * @return
     */
    private String insertShortId(ShortUrlRequest request, String urlHash) {
        SShortUrl insert = createInsertDO(request, urlHash);
        int insertRsp = sShortUrlMapper.insertSelective(insert);
        if (insertRsp > 0) {
            return insert.getShortId();
        } else {
            return null;
        }
    }

    /**
     * 生成insert Bean
     * @param request
     * @param urlHash
     * @return
     */
    private SShortUrl createInsertDO(ShortUrlRequest request, String urlHash) {
        SShortUrl insert = new SShortUrl();
        insert.setShortId(StringUtils.isEmpty(request.getCustomKey()) ? UUID.randomUUID().toString().trim().replace("-", "") : request.getCustomKey());
        insert.setSourceUrlHash(urlHash);
        insert.setSourceUrl(request.getUrl());
        return insert;
    }

}
