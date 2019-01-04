package com.lyc.tmp;

import java.lang.reflect.InvocationTargetException;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/6
 * TIME 14:53
 */
public class MyInterceptor implements Interceptor{
    @Override
    public boolean before() {
        System.out.println("before ....");
        return true;
    }

    @Override
    public void after() {
        System.out.println("aftetr....");
    }

    @Override
    public Object around(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around before...");
        Object o = invocation.proceed();
        System.out.println("around after ...");
        return o;
    }

    @Override
    public void afterRetrurning() {
        System.out.println("after returnign ...");

    }

    @Override
    public void afterThrowing() {
        System.out.println("after throwing...");

    }

    @Override
    public boolean useAround() {
        return true;
    }
}
