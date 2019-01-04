
package com.lyc.controller;
import com.lyc.entity.ClientEntity;
import com.lyc.mapper.BookMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class AuthorsController {
    @Autowired
    BookMapper bookMapper;

    @RequestMapping("/favorAuthors")
    public String register(ModelMap modelMap){


        modelMap.addAttribute("client", new ClientEntity());

        return "favorAuthors";
    }

    @RequestMapping("/getFac")
    public void getFac(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
        //TODO: 在这里加上判断Fac的逻辑
        // FIXME: 这里有一点问题

        Integer i = Integer.parseInt(request.getParameter("q"));
        Integer[] ans = fracInteger(i);
        OutputStream o = httpServletResponse.getOutputStream();
        for(int k =0;k< ans.length;k++) {
            String s = ans[k] + "";
            o.write(s.getBytes());
            o.write("\n".getBytes());

        }
    }

    private Integer[] fracInteger(Integer i) {
        Integer a = 0 , b = 0;
        for (int j = 0; j < i; j++) {
            if ((j < i / 4)) {
                a++;
            } else {
                b++;
            }
        }
        return new Integer[]{a, b};
    }

}
