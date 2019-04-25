package com.lyc.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lyc.entity.BookEntity;
import com.lyc.entity.Dbook;
import com.lyc.mapper.BookMapper;
import com.lyc.mapper.DBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CREATE WITH Lenovo
 * DATE 2019/1/9
 * TIME 10:28
 */
@Service
@Transactional(readOnly = true)
public class DBookService {

    @Autowired
    private DBookMapper dBookMapper;

    @Autowired
    private BookMapper bookMapper;

    public Page<Dbook> findByPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        return dBookMapper.findByPage();
    }

    public Page<BookEntity> findAll(int pageNo, int pageSize, String title) {
        PageHelper.startPage(pageNo, pageSize);
        return dBookMapper.filtering(title);
    }
}
