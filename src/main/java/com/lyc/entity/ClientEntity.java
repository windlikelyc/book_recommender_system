package com.lyc.entity;

import com.lyc.validator.UsernameDup;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;

@Data
@Component
public class ClientEntity {
    private Long id;
    private String name;
    private String phone;
    private int age;
    private String sex;
    private String job;
    private String area;
    private String nick;
    private String attributes;
//    @Size(min = 3,max = 10,message = "用户名必须在3到10之间")
    @UsernameDup
    private String username;
    @Size(min = 6,message = "密码长度必须大于等于6位")
    private String passwd;
    private String photo;
    private String email;
}
