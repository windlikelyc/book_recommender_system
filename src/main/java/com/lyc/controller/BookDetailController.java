package com.lyc.controller;

import com.lyc.entity.*;
import com.lyc.mapper.ClientMapper;
import com.lyc.service.ClientService;
import com.lyc.service.DoubanService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
        BookEntity bookEntity = doubanService.getDetaild(request.getParameter("id"));
        map.addAttribute("book", bookEntity);
        ClientFeedBack cb = new ClientFeedBack();
        cb.setBookId(request.getParameter("id"));
        map.addAttribute("feedback", cb);
        clientService.addToUserHistory(request.getParameter("id"), 0); // 点击记录
        return "bookdetail";
    }

    @PostMapping("/bookFeedback") // 对图书的评分、评论等信息
    public String addBookFeedBack(@ModelAttribute(value = "feedback") ClientFeedBack feedback,ModelMap modelMap, HttpServletRequest request) {

        System.out.println(feedback.getComment());
        System.out.println(feedback.getRating());

        ClientFeedBack clientFeedBack = new ClientFeedBack();
        clientFeedBack.setBookId(feedback.getBookId());
        clientFeedBack.setComment(feedback.getComment());
        clientFeedBack.setRating(feedback.getRating());

        clientService.insertComments(clientFeedBack);

        return "redirect:/bookDetail?id=" + feedback.getBookId();
    }

    @RequestMapping("/bookLabelFeedback")
    public String bookLabelFeedBack(ModelMap map, HttpServletRequest request) {

        ClientFeedBack clientFeedBack = new ClientFeedBack();
        clientFeedBack.setTag(request.getParameter("label"));
        clientFeedBack.setBookId(request.getParameter("id"));

        clientService.insertLabels(clientFeedBack);

        return "redirect:/bookDetail?id=" + request.getParameter("id");
    }

}
