/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csu.fullerton.cpsc531.obj;

/**
 * @author Liam
 */
public class Department implements Comparable<Object>{
    private String dept_code;
    private String dept_name;

    public Department(String dept_code, String dept_name) {
        this.dept_code = dept_code;
        this.dept_name = dept_name;
    }

    public String getDept_code() {
        return dept_code;
    }

    public void setDept_code(String dept_code) {
        this.dept_code = dept_code;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    @Override
    public String toString() {
        return this.dept_code;
    }

    @Override
    public int compareTo(Object t) {
        return this.dept_code.compareTo(((Department)t).getDept_code());
    }
    
    
}
