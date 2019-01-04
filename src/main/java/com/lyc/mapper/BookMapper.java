package com.lyc.mapper;

import com.lyc.entity.BookEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Random;

@Mapper
public interface BookMapper {

    /**
     *  查找这样一些书籍，返回结果是id，标题和图片
     *  供主页使用,默认返回12本书
     */
    @SelectProvider(type = BookDaoProvider.class, method = "findNewBooksByOffset")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "image", column = "img")
    })
    List<BookEntity> findNewBooksByOffset(Integer offset,Integer num);
    class BookDaoProvider {
        public String findNewBooksByOffset(Integer offset,Integer num) {
            String sql = "Select book.id,title,img from book left join book_img on book.id = book_img.id " +
                    "LEFT JOIN book_summery on book.id = book_summery.id " +
                    "WHERE book_summery != \"\" and LENGTH(book_summery) > 140\n" +
                    "AND img != \"\"" +
                    " limit ";
            String condition = offset + "," + num;
            return sql + " " +  condition;
        }
    }

    /**
     *  查找一个书籍
     * @param
     * @return
     */
    @Select("SELECT book.id,title,img FROM book left join book_img on book.id = book_img.id where book.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "image", column = "img")
    })
    BookEntity findBooksById(@Param("id") String id);


    /**
     * 查用户不喜欢的图书的id
     *
     * @return
     */
    @Select("SELECT DISTINCT(book_id) FROM `client_book_history` WHERE user_name = #{username} AND type = 3")
    List<String> findUserDislikeBooks(@Param("username") String username);


    @Select("SELECT * FROM book")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title")
    })
    List<BookEntity> getAll();


}
