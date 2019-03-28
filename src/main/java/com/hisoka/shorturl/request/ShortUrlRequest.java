package com.hisoka.shorturl.request;

import java.io.Serializable;

/**
 * @author Hinsteny
 * @version $ID: ShortUrlRequest 2019-03-28 20:10 All rights reserved.$
 */
public class ShortUrlRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 【必填】原始URL地址，最长不超过500个字符
     */
    private String url;

    /**
     * 【可选】自定义短链名字[a-zA-Z0-0]{1, 20}
     */
    private String customKey;

    /**
     * 是否需要带上 https://这个 协议head，默认是（0否，1是）
     */
    private Integer withHttpHead = 1;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomKey() {
        return customKey;
    }

    public void setCustomKey(String customKey) {
        this.customKey = customKey;
    }

    public Integer getWithHttpHead() {
        return withHttpHead;
    }

    public void setWithHttpHead(Integer withHttpHead) {
        this.withHttpHead = withHttpHead;
    }
}
