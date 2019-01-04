package com.lyc.enums;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/4
 * TIME 15:00
 */
public enum SloganEnum {

    WELCOME("欢迎来到图书推荐系统"),
    NEWBOOKS("最新获取的图书"),
    MYBOOKLIST("我的图书收藏列表"),
    RECOMMEND("为您推荐的相似图书"),
    SIMILAR("已为您找到相似图书");

    private SloganEnum(String slogan) {
        this.slogan = slogan;
    }

    private String slogan;

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
}
