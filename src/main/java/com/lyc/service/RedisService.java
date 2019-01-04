package com.lyc.service;

import com.lyc.entity.BookEntity;
import com.lyc.mapper.BookMapper;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/27
 * TIME 19:20
 */
@Service
public class RedisService {


    @Autowired
    BookMapper bookMapper;

    public List<BookEntity> getUserCachedBooks() {
        Jedis jedis = new Jedis("39.106.39.216", 6379);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        String prefix = username + "cachedBooks";

        Set<String> allKeys = jedis.keys(prefix + "*");
        if (allKeys == null || allKeys.size() == 0) return null;

        Map<String, Response<Map<String, String>>> repnses = new HashMap<>(allKeys.size());

        List<BookEntity> bookEntities = new ArrayList<>();

        for (String key : allKeys) {
            Map<String,String> map = jedis.hgetAll(key);
            BookEntity bookEntity = new BookEntity();
            bookEntity.setId(map.get("id"));
            bookEntity.setImage(map.get("image"));
            bookEntity.setTitle(map.get("title"));
            bookEntity.setRank(Integer.parseInt(map.get("rank")));
            bookEntities.add(bookEntity);
        }
        jedis.close();
        bookEntities = bookEntities.stream().sorted((b1, b2) -> b1.getRank() > b2.getRank() ? 1 : -1).collect(Collectors.toList());
        return bookEntities;
    }

    public void setUserCachedBooks(List<BookEntity> books) {
        Jedis jedis = null;

        try {
            jedis = new Jedis("39.106.39.216", 6379);

            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            for (int i = 0; i < books.size(); i++) {
                jedis.hset(username + "cachedBooks" + i, "id", books.get(i).getId());
                jedis.hset(username + "cachedBooks" + i, "title", books.get(i).getTitle());
                jedis.hset(username + "cachedBooks" + i, "image", books.get(i).getImage());
                jedis.hset(username + "cachedBooks" + i, "rank", i+"");
            }

        } catch (Exception e) {

        }finally {
            jedis.close();
        }
    }

    // 过滤掉首页随机推荐上的 redis 缓存
    public void filterDislikeBooks(String username,String bookid) {

        final Jedis jedis = new Jedis("39.106.39.216", 6379);
        try {
            Set<String> keys = jedis.keys(username + "*");

            Iterator<String> i = keys.iterator();
            String thekey = null;
            while (i.hasNext()) {
                String ss = i.next();
                Map replacement = jedis.hgetAll(ss);
                if (replacement.get("id").equals(bookid)) {
                    thekey = ss;

                    List<BookEntity> BL = null;
                    do {
                        BL = bookMapper.findNewBooksByOffset(new Random().nextInt(500), 1);
                    }
                    while (BL == null || BL.get(0).getImage() == null || BL.get(0).getTitle() == null || BL.get(0).getId() == null);
                    jedis.hset(ss, "id", BL.get(0).getId());
                    jedis.hset(ss, "title", BL.get(0).getTitle());
                    jedis.hset(ss, "image", BL.get(0).getImage());
                }
            }
        }finally {
            jedis.close();
        }

    }

    public void addToMap(Map<String, String> maps,String key) {

        Jedis jedis = null;
        try {
            jedis = new Jedis("39.106.39.216", 6379);

            for (Map.Entry<String ,String> e : maps.entrySet()) {
                jedis.hset(key, e.getKey(), e.getValue());
            }
        }finally {
            jedis.close();
        }

    }

    public String hget(String key,String feild) {
        Jedis jedis = null;
        try {
            jedis = new Jedis("39.106.39.216", 6379);
            return jedis.hget(key,feild);
        }finally {
            jedis.close();
        }
    }



}


