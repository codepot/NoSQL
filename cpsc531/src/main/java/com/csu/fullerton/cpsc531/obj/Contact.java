/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csu.fullerton.cpsc531.obj;

import java.nio.ByteBuffer;
import java.sql.Blob;
import java.util.UUID;

/**
 *
 * @author Liam
 */
public class Contact {

    private UUID contactId; // java.util.UUID uuid = java.util.UUID.randomUUID();
    private String firstname = "";
    private String lastname = "";
    private String company = "";
    private String address1 = "";
    private String address2 = "";
    private String email = "";
    private String telephone = "";
    private String cellphone = "";
    private String role = ""; //employee, manager, customer
    private double salary = 0.00;
    private ByteBuffer photo = null;
    private UUID report_to;
    private String department_code = "";

    public Contact() {
    }

    public Contact(String firstname, String lastname, String company, String address1, String address2, String email, String telephone, String cellphone, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.company = company;
        this.address1 = address1;
        this.address2 = address2;
        this.email = email;
        this.telephone = telephone;
        this.cellphone = cellphone;
        this.role = role;
    }

    public UUID getContactId() {
        return contactId;
    }

    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public ByteBuffer getPhoto() {
        return photo;
    }

    public void setPhoto(ByteBuffer photo) {
        this.photo = photo;
    }

    public UUID getReport_to() {
        return report_to;
    }

    public void setReport_to(UUID report_to) {
        this.report_to = report_to;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    @Override
    public String toString() {
        return this.lastname + ", " + this.firstname;
    }
}
