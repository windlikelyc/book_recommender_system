package com.lyc.algorithm;

public class Attibutes {

    // 每个属性的名称，如ABC
    private int size;
    private Node[] nodes;

    class Node {
        String name;
        int exist;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
