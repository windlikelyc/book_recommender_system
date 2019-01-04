package com.lyc.algorithm;

// 对论文方法的一些实现

import java.util.concurrent.ThreadPoolExecutor;

public class PaperImpl {

    // 项目和属性矩阵
    private int[][] item_attributes;

    // 属性个数，一个属性可能有很多的值
    private int p ;

    // 每个属性的长度
    private int size_of_attribuite_a = 3;
    private int size_of_attribuite_b = 2;
    private int size_of_attribuite_c = 2;

    public void init() {
        // 第一步，构建项目-属性矩阵
        int[][] tmp =
                {{1, 1, 0, 1, 0, 0, 1},
                {0, 1, 1, 0, 1, 1, 0},
                {1, 0, 1, 0, 1, 0, 1},
                {0, 1, 1, 1, 0, 1, 0}};

        item_attributes = tmp;
        p = 3;
    }


    /** 第一步
     * 返回Dice 相似度，其中i，j是项目id
     * @param i
     * @param j
     * @param l 长度
     * @return
     */
    public double getDS(int i, int j,int l) {
        double n_i = 0,n_j = 0 ,n_ij = 0;
        for (int x = 0; x < l ; x++) {
            if (item_attributes[i][x] == 1) {
                n_i++;
            }
            if (item_attributes[j][x] == 1) {
                n_j++;
            }
            if (item_attributes[i][x] == 1 && item_attributes[j][x] == 1) {
                n_ij++;
            }
        }
        return 2 * n_ij / (l+l);
    }

    /** 第二步
     * 返回MAS
     *
     * @param
     */
    public double getMAS(int i, int j) {

        double a = getDS(i, j, size_of_attribuite_a);
        double b = getDS(i, j, size_of_attribuite_b);
        double c = getDS(i, j, size_of_attribuite_c);
        return (a+b+c) / p;
    }

    /** 第三步
     * 返回MN
     * @param i 项目id
     * @param j 项目id
     * @return
     */
    public double getMN(int i, int j) {

        return 0;
    }

    public <T> void show(T[][] matrix) {
        for(int i = 0 ; i < matrix.length ;i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }



    public static void main(String[] args) {
        PaperImpl paper = new PaperImpl();
        paper.init();
        Double[][] ma = new Double[4][4];
        for(int i = 0 ; i < 4 ; i++) {
            for (int j = 0; j < 4; j++) {
                ma[i][j] = paper.getMAS(i, j);
            }
        }
        paper.show(ma);
    }
}
