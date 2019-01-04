package com.lyc.service;

import com.lyc.entity.BookEntity;
import com.lyc.entity.Tag;
import org.python.antlr.ast.Str;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/24
 * TIME 13:14
 */
@Service
public class DoubanService {

    public BookEntity getDetaild(String bookid)  {
        String pre = "https://api.douban.com/v2/book/";
        String apikey = "?apikey=0b2bdeda43b5688921839c8ecb20399b";
        RestTemplate restTemplate = new RestTemplate();
        BookEntity be = restTemplate.getForObject(pre + bookid + apikey, BookEntity.class);
        be.setAuthorStr(be.getAuthor().toString());
        List<String> tn = be.getTags().stream().map(o -> o.getName()).collect(Collectors.toList());
        be.setTagsStr(String.join(",", tn));
        replaceImgWithImages(be);
        return be;
    }

    // 讲一个bookEntyity的图片替换成有效的 图片链接
    public void replaceImgWithImages(BookEntity be) {
        be.setImage(be.getImage().replace("view/subject/m/public","lpic"));
    }


}
