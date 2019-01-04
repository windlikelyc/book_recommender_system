package com.lyc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BookLotteryController {


    @RequestMapping("/bookLottery")
    public String bookLottery(){
        System.out.println("用户积分增加多少多少");
        return "bookLottery";
    }

}
