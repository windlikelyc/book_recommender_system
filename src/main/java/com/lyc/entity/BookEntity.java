package com.lyc.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class BookEntity {
    private String id;
    private String image;
    private String title;
    private String subtitle;
    private Rating rating;
    private List<String> author;
    private String pubdate;
    private List<Tag> tags;
    private String catalog;
    private String author_intro;
    private String pages;
    private String alt;
    private String publisher;
    private String summery;
    private String price;

    // 以下是为了方便加入的额外属性
    private String authorStr;
    private String tagsStr;

    // 图书的大小
    private int rank;
}
