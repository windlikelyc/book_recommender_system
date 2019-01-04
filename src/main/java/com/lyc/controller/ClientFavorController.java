package com.lyc.controller;

import com.lyc.entity.ClientHistory;
import com.lyc.enums.SloganEnum;
import com.lyc.service.ClientService;
import com.lyc.service.DoubanService;
import com.lyc.service.RedisService;
import com.lyc.tmp.testAspectJ.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// 控制首页用户对某一本书是否喜欢
@Controller
public class ClientFavorController {

    @Autowired
    ClientService clientService;

    @Autowired
    DoubanService doubanService;

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping(value = "/favor")
    public String findRelevance(ModelMap modelMap, HttpServletRequest request) {
        List<ClientHistory> records = clientService.getMyFavourateBooks();
        modelMap.put("records", records);


        modelMap.addAttribute("slogan", SloganEnum.MYBOOKLIST.getSlogan());
        modelMap.addAttribute("mylist_active_flag", "active");

        return "mybooklists";
    }

    @RequestMapping(value = "/deletefavor")
    public String deletefavor(@RequestParam("id") int id) {

        clientService.deleteHistoryById(id);

        return "redirect:favor";
    }

    @RequestMapping(value = "/add")
    public String addToHistory(ModelMap modelMap, HttpServletRequest request) {

        if (request.getParameter("type") == null || request.getParameter("id") == null) {
            throw new RuntimeException();
        }
        clientService.addToUserHistory(request.getParameter("id"), Integer.parseInt(request.getParameter("type")));
        if (request.getParameter("type").equals("3")) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            redisService.filterDislikeBooks(userDetails.getUsername(), request.getParameter("id"));
        }
        return "redirect:welcome";
    }


}
