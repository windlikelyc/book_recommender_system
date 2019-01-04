package com.lyc.mapper;

import com.lyc.entity.ClientEntity;
import com.lyc.entity.ClientHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

// 用于首页登陆的控制器
@Mapper
public interface ClientMapper {
    @Insert(value = "insert into client(id,username, passwd) values (#{id},#{username}, #{passwd})")
    boolean insert(ClientEntity clientEntity);

    @Select(value = "Select * from client where username = #{username}")
    ClientEntity selectByUsername(String username);


    @Insert(value = "insert into client_book_history(book_id,user_name,type,create_time) values (#{bookId},#{userName},#{type},#{createTime})")
    boolean insertHistory(ClientHistory clientHistory);

    @Select(value = "SELECT h.id,book_id,title,create_time,type FROM client_book_history h LEFT JOIN book b on h.book_id = b.id WHERE user_name = #{username} and type != 0")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "bookId", column = "book_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "type" ,column = "type")
    })
    List<ClientHistory> selectBooksByUsername(String username);

    @Delete(value = "DELETE FROM client_book_history where id = #{id}")
    void deleteHistoryById(int id);
}
