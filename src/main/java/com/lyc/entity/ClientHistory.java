package com.lyc.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/24
 * TIME 15:54
 */

@Data
@Component
public class ClientHistory {

    private int id;
    private String bookId;
    private String userName;
    private int type;
    private Date createTime;

    // left join 得来的属性
    private String title;

    // 中文时间
    private String chinaTime;

    // 中文状态
    private String status;
}
