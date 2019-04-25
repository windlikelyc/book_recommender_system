package com.lyc.mapper;

import com.github.pagehelper.Page;
import com.lyc.entity.BookEntity;
import com.lyc.entity.Dbook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * CREATE WITH Lenovo
 * DATE 2019/1/9
 * TIME 10:21
 */
@Mapper
public interface DBookMapper {
    Page<Dbook> findByPage();

    Page<BookEntity> filtering(@Param("t") String t);
}
