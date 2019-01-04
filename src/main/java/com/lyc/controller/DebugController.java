package com.lyc.controller;

import com.lyc.entity.ClientEntity;
import com.lyc.service.RedisService;
import com.lyc.tmp.testAspectJ.User;
import com.lyc.tmp.testAspectJ.UserService;
import com.lyc.tmp.testAspectJ.UserVaildator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/20
 * TIME 17:28
 */
@Controller
public class DebugController {

    @Autowired
    RedisService redisService;

    @Autowired
    UserService userService;

    @RequestMapping("/debug")
    @ResponseBody
    public String debug(ModelMap modelMap){
        redisService.filterDislikeBooks("fengliulyc", "1007760");
        return "这是用来测试的页面";
    }

    @RequestMapping("/testap")
    @ResponseBody
    public User printUser(Long id, String userName, int age) {
        User user = new User();
        user.setId(id);
        user.setName(userName);
        user.setAge(age);
        userService.printUser(null);
        return user;
    }

    @RequestMapping("/testap2")
    @ResponseBody
    public User printUser2(Long id, String name, int age) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        UserVaildator userVaildator = (UserVaildator) userService;
        if ((userVaildator).validate(null)) {
            userService.printUser(user);
        }
        return user;
    }

}
