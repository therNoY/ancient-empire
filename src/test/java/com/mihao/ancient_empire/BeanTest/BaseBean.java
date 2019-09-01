package com.mihao.ancient_empire.BeanTest;

public class BaseBean {

    public void test(){
        print();
    }

    public void print() {
        System.out.println("我是 f");
    }

    public static void main(String[] args) {
        BaseBean baseBean = new ChildBean();
        baseBean.test();
    }
}
