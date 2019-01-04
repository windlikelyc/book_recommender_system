package com.lyc.controller;

import com.lyc.entity.BookEntity;
import com.lyc.enums.SloganEnum;
import com.lyc.mapper.NewBookMapper;
import com.lyc.service.DoubanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class LatestBooksController {

    @Autowired
    NewBookMapper newBookMapper;
    @Autowired
    DoubanService doubanService;

    @RequestMapping("/getLatesBooks")
    public String getLatesBooks(ModelMap map){

        List<BookEntity> newbooklist = newBookMapper.findNewBooks();
        newbooklist.forEach(doubanService::replaceImgWithImages);

        map.addAttribute("newbook", newbooklist);
        map.addAttribute("rows", Arrays.asList(0, 1, 2)); // 行数
        map.addAttribute("cols_big", Arrays.asList(0, 1)); // 一行分为几列
        map.addAttribute("cols_sm", Arrays.asList(0, 1, 2)); // 每一列有几个项目

        map.addAttribute("slogan", SloganEnum.NEWBOOKS.getSlogan());
        map.addAttribute("new_book_flag", "active");

        return "latestbooks";
    }
}
