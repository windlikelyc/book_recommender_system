package com.lyc.tmp.testAspectJ;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/6
 * TIME 16:12
 */
public class UserValidatorImpl implements UserVaildator {
    @Override
    public boolean validate(User user) {
        System.out.println("引入新的接口" + UserVaildator.class.getSimpleName());

        return user != null;
    }
}
