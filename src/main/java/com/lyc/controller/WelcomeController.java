package com.lyc.controller;

import com.github.pagehelper.Page;
import com.lyc.Utils.IdGenUtil;
import com.lyc.Utils.MD5Util;
import com.lyc.entity.*;
import com.lyc.enums.SloganEnum;
import com.lyc.mapper.BookMapper;
import com.lyc.mapper.ClientMapper;
import com.lyc.mapper.NewBookMapper;
import com.lyc.model.PagerModel;
import com.lyc.service.ClientService;
import com.lyc.service.DBookService;
import com.lyc.service.RedisService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class WelcomeController {

    @Autowired
    BookMapper bookMapper;
    @Autowired
    NewBookMapper newBookMapper;
    @Autowired
    ClientMapper clientMapper;
    @Autowired
    RedisService redisService;

    @Autowired
    ClientService clientService;

    @Autowired
    DBookService dBookService;

    // inject via application.properties
    @Value("${welcome.message:test}")
    private String message = "Hello World";

    @RequestMapping("/welcome") // 获取用户书籍，放到redis例
    public String welcome(@ModelAttribute(value = "searchbook") SearchBook searchBook,
                          @RequestParam("t") Optional<String> t,
                          @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam("page") Optional<Integer> page,
            ModelMap map, HttpServletRequest request) {

//        List<BookEntity> newbooklist = redisService.getUserCachedBooks();

//        if (request.getParameter("refresh") != null || newbooklist == null) {
//            newbooklist = bookMapper.findNewBooksByOffset(new Random().nextInt(5000),18);
//            redisService.setUserCachedBooks(newbooklist);
//        }

        if (searchBook == null) {
            map.addAttribute("searchbook", new SearchBook());
        } else {
            if (t.isPresent()) {
                searchBook.setTitle(t.get());
            }
            map.addAttribute("searchbook", searchBook);
        }


        System.out.println(searchBook.getTitle() == null ? "空检索标题" : searchBook.getTitle());

        int evalPageSize = pageSize.orElse(18);
        int evalPageNo = page.orElse(0) < 1 ? 0 : page.get() - 1;

        Page<BookEntity> newbooklist = dBookService.findAll(evalPageNo, evalPageSize , searchBook.getTitle() == null ? t.orElse(null) : searchBook.getTitle());
        PagerModel pager = new PagerModel(newbooklist.getPages(), newbooklist.getPageNum(), 3);
        if (pager.getStartPage() > pager.getEndPage()) {
            pager.setStartPage(pager.getEndPage());
        }

        map.addAttribute("newbook", newbooklist);

        map.addAttribute("rows", Arrays.asList(0, 1, 2)); // 行数
        map.addAttribute("cols_big", Arrays.asList(0, 1)); // 一行分为几列
        map.addAttribute("cols_sm", Arrays.asList(0, 1, 2)); // 每一列有几个项目

        map.addAttribute("pageSizes", new int[]{5, 10});
        map.addAttribute("pager", pager);
        map.addAttribute("selectedPageSize", evalPageSize);

        map.addAttribute("slogan", SloganEnum.WELCOME.getSlogan());
        map.addAttribute("welcome_active_flag", "active");

        return "welcome";
    }


    @RequestMapping("/login")
    public String login(ModelMap modelMap) {
        modelMap.addAttribute("client", new Client());
        return "login";
    }

    /**
     * 用户注册接口
     *
     * @param client
     * @param bindingResult
     * @return
     */
    @PostMapping("/registerVerify")
    public String registerVerify(@Valid @ModelAttribute(value = "client") ClientEntity client, BindingResult bindingResult, ModelMap modelmap) {
        if (bindingResult.hasErrors()) {
            modelmap.addAttribute("client", client);
            return "/register";
        } else {
            client.setId(IdGenUtil.get().nextId());
            client.setPasswd(MD5Util.encode(client.getPasswd()));
            System.out.println(client);
            clientMapper.insert(client);
        }
        modelmap.addAttribute("client", new Client());
        return "login";
    }

    @RequestMapping("/register")
    public String register(ModelMap modelMap) {
        modelMap.addAttribute("client", new ClientEntity());
        return "register";
    }



}