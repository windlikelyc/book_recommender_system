package com.lyc.controller;


import com.lyc.entity.BookEntity;
import com.lyc.entity.Client;
import com.lyc.entity.ClientHistory;
import com.lyc.enums.SloganEnum;
import com.lyc.mapper.BookMapper;
import com.lyc.service.DoubanService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.util.*;

// 这是一个相似书籍的控制器
// 可以找到相似书籍，加入书单，表示用户喜欢这本书或者不喜欢这本书
@Controller
public class RelevanceController {

    private String pyServerAddress = "http://127.0.0.1";
    private String pyServerPort = "8082";

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private DoubanService doubanService;


    @RequestMapping(value = "/relevance")
    public String findRelevance(ModelMap modelMap, HttpServletRequest request) {

        RestTemplate restTemplate = new RestTemplate();

        String serverUrl = pyServerAddress + ":" + pyServerPort;

        String response = restTemplate.getForObject(serverUrl + "?q=" + request.getParameter("id"), String.class);
        String[] recommendedBookIds = response.split(" ");
        List<BookEntity> ids = new ArrayList<>();
        for (String s : recommendedBookIds)
            ids.add(doubanService.getDetaild(s));


        modelMap.put("newbook", ids);
        modelMap.put("bt", doubanService.getDetaild(request.getParameter("id")).getTitle());

        modelMap.addAttribute("rows", Arrays.asList(0)); // 行数
        modelMap.addAttribute("cols_big", Arrays.asList(0, 1)); // 一行分为几列
        modelMap.addAttribute("cols_sm", Arrays.asList(0, 1, 2)); // 每一列有几个项目

        modelMap.addAttribute("slogan", SloganEnum.SIMILAR.getSlogan());


        return "relevance";
    }


}
