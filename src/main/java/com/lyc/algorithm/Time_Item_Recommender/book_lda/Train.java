package com.lyc.algorithm.Time_Item_Recommender.book_lda;


public class Train {
	int[][] trainI;//用户-序列-项目
    double[][] trainT;//用户-序列-时间
    double[][] trainJimT;//用户-序列-时间
    String[][] trainW;// 用户-序列-单词集合
    String[][] trainC;//用户-序列-集合集合

    String[][] trainAu; // 用户-序列-作者
    String[][] trainPub; // 用户-序列-出版商

    int[][] trainS;//用户-序列-星期几
    int[][] trainL;
}
