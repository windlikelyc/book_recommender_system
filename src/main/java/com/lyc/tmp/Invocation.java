package com.lyc.tmp;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/5
 * TIME 21:05
 */
@Getter
@Setter
public class Invocation {

    private Object[] params;
    private Method method;
    private Object target;

    public Invocation(Object target, Method method, Object[] params) {
        this.params = params;
        this.method = method;
        this.target = target;
    }

    public Object proceed() throws InvocationTargetException,IllegalAccessException {
        return method.invoke(target, params);
    }
}
