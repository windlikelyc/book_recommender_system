package com.lyc.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * CREATE WITH Lenovo
 * DATE 2018/11/24
 * TIME 13:29
 */
@Data
@Component
public class Rating {
    private int max;
    private int numRaters;
    private String average;
    private int min;
}
