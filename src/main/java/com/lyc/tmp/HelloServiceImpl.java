package com.lyc.tmp;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/5
 * TIME 21:01
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        if (name == null || name.trim() == "") {
            throw new RuntimeException("parameter is null");
        }
        System.out.println("hello" + name);
    }
}
