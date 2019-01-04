package com.lyc.controller;

import com.lyc.algorithm.Time_Item_Recommender.book_lda.JIMVersion3;
import com.lyc.entity.BookEntity;
import com.lyc.enums.SloganEnum;
import com.lyc.mapper.BookMapper;
import com.lyc.service.DoubanService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/28
 * TIME 14:59
 */
@Controller
public class RecommendController {

    @Autowired
    JIMVersion3 jimVersion3;
    @Autowired
    DoubanService doubanService;
    @Autowired
    BookMapper bookMapper;

    @RequestMapping(value = "/recommend")
    public String findRelevance(ModelMap map, HttpServletRequest request) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> results = jimVersion3.getRecommendedResult(userDetails.getUsername());

        List<String> dislikes = bookMapper.findUserDislikeBooks(userDetails.getUsername());
        System.out.println(results);
        List<String> filteredresult  = results.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return !dislikes.contains(s);
            }
        }).collect(Collectors.toList());
        System.out.println(filteredresult);

        List<BookEntity> newbooklist = filteredresult.stream().map(o->doubanService.getDetaild(o)).collect(Collectors.toList());

        map.addAttribute("newbook", newbooklist);
        map.addAttribute("rows", Arrays.asList(0)); // 行数
        map.addAttribute("cols_big", Arrays.asList(0, 1)); // 一行分为几列
        map.addAttribute("cols_sm", Arrays.asList(0, 1, 2)); // 每一列有几个项目


        map.addAttribute("slogan", SloganEnum.RECOMMEND.getSlogan());
        map.addAttribute("recommend_active_flag", "active");


        return "recommend";
    }


}
