package com.lyc.service;

import com.lyc.entity.BookEntity;
import com.lyc.entity.ClientFeedBack;
import com.lyc.entity.Tag;
import com.lyc.mapper.ClientMapper;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
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

    @Resource
    ClientMapper clientMapper;

    public BookEntity getDetaild(String bookid)  {
        String pre = "https://api.douban.com/v2/book/";
        String apikey = "?apikey=0b2bdeda43b5688921839c8ecb20399b";
        RestTemplate restTemplate = new RestTemplate();
        BookEntity be = restTemplate.getForObject(pre + bookid + apikey, BookEntity.class);
        be.setAuthorStr(be.getAuthor().toString());
        List<String> tn = be.getTags().stream().map(o -> o.getName()).collect(Collectors.toList());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<String> labels = clientMapper.selectLabelsOfBookByUser(bookid, userDetails.getUsername());

        for(int i = 0 ; i < tn.size() ; i++) {
            if (labels.contains(tn.get(i))) {
                tn.set(i, "*" + tn.get(i));
            }
        }

        be.setTagsStr(String.join(",", tn));
        be.setLabels(tn);
        replaceImgWithImages(be);

        List<ClientFeedBack> allComments = clientMapper.selectAllCommentsByBookId(bookid);
        List<String> allCommentsLine = allComments.stream().map(o -> {
            StringBuilder sb = new StringBuilder();
            sb.append("昵称:  ");
            sb.append(o.getUserName() + " ");
            sb.append("评分: ");
            sb.append(o.getRating() + " ");
            sb.append("评论: ");
            sb.append(o.getComment() + " " );
            sb.append("时间: ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            String date = dateFormat.format(o.getCreateTime());
            sb.append(date);
            return sb.toString();
        }).collect(Collectors.toList());

        be.setAllComments(allCommentsLine);

        return be;
    }

    // 讲一个bookEntyity的图片替换成有效的 图片链接
    public void replaceImgWithImages(BookEntity be) {
        be.setImage(be.getImage().replace("view/subject/m/public","lpic"));
    }


}
