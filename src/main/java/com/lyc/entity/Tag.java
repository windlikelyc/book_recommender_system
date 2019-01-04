package com.lyc.entity;

import lombok.Data;
import org.python.antlr.ast.Str;
import org.springframework.stereotype.Component;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/24
 * TIME 13:31
 */
@Data
@Component
public class Tag {

    private int count;
    private String name;
    private String title;
}
