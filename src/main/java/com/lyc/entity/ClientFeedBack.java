package com.lyc.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * CREATE WITH Lenovo
 * DATE 2019/3/8
 * TIME 20:26
 */

// 该值对象含了用户对图书的标签、评论、评分等信息
@Data
@Component
public class ClientFeedBack {

    private int id;

    private String userId;
    private String bookId;

    private String userName;

    private String replyId;


    private String comment;

    private String tag;

    private float rating;

    private Date createTime;

}
