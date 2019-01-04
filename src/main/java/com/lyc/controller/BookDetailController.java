package com.lyc.controller;

import com.lyc.entity.ClientHistory;
import com.lyc.mapper.ClientMapper;
import com.lyc.service.ClientService;
import com.lyc.service.DoubanService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/24
 * TIME 12:53
 */
@Controller
public class BookDetailController {

    @Autowired
    DoubanService doubanService;
    @Autowired
    ClientService clientService;


    @RequestMapping("/bookDetail")
    public String bookLottery(ModelMap map, HttpServletRequest request) {
        map.addAttribute("book", doubanService.getDetaild(request.getParameter("id")));
        clientService.addToUserHistory(request.getParameter("id"), 0);
        return "bookdetail";
    }

}
