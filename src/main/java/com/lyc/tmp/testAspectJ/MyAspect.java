package com.lyc.tmp.testAspectJ;

import org.aspectj.lang.annotation.*;

/**
 * CREATE WITH Lenovo
 * DATE 2018/12/6
 * TIME 15:34
 */
@Aspect
public class MyAspect {


    @Pointcut("execution(* com.lyc.tmp.testAspectJ.UserServiceImpl.printUser(..))")
    public void pointCut() {
    }


    @Before("execution(* com.lyc.tmp.testAspectJ.UserServiceImpl.printUser(..))")
    public void before() {
        System.out.println("before .....");
    }

    @After("execution(* com.lyc.tmp.testAspectJ.UserServiceImpl.printUser(..))")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("execution(* com.lyc.tmp.testAspectJ.UserServiceImpl.printUser(..))")
    public void afterReturning() {
        System.out.println("afterReturning...");
    }

    @AfterThrowing("execution(* com.lyc.tmp.testAspectJ.UserServiceImpl.printUser(..))")
    public void afterThrowing() {
        System.out.println("afterthrowing.....");
    }

    @DeclareParents(value = "com.lyc.tmp.testAspectJ.UserServiceImpl+", defaultImpl = UserValidatorImpl.class)
    public UserVaildator userVaildator;
}
