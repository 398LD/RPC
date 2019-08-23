package com.kexun.entity;

public class StudentImpl implements Student {
    @Override
    public void lern() {

        System.out.println("我爱学习");

    }

    @Override
    public String tall() {
        return "学生说话";
    }
}
