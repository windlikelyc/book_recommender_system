package com.lyc.tmp;



import java.lang.reflect.InvocationTargetException;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/5
 * TIME 21:02
 */
public interface Interceptor {

    public boolean before();

    public void after();

    public Object around(Invocation invocation) throws InvocationTargetException, IllegalAccessException;

    public void afterRetrurning();

    public void afterThrowing();

    boolean useAround();

}
