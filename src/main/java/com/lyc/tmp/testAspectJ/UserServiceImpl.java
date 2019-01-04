package com.lyc.tmp.testAspectJ;

import org.springframework.stereotype.Service;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/6
 * TIME 15:32
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public void printUser(User user) {
        if (user == null) {
            throw new RuntimeException("检查用户参数是否为空");
        }
        System.out.println("id = " + user.getId());
        System.out.println("name = " + user.getName());
        System.out.println("age = " + user.getAge());
    }
}
