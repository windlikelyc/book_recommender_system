package com.lyc.mapper;

import com.lyc.entity.BookEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewBookMapper {
    /**
     *  查找最新的图书
     * @param
     * @return
     */
    @Select("SELECT * from book_new order by pubdate desc limit 18")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "image", column = "img")
    })
    List<BookEntity> findNewBooks();
}
