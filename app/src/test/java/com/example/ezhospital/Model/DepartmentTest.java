package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DepartmentTest {




    @Before
    public void setUp() throws Exception {
        Department department=new Department();
    }

    @Test
    public void setName() {
        String name="name";
    }

    @Test
    public void setAddress() {
        String address="address";
    }

    @Test
    public void setWebsite() {
        String website="website";
    }

    @Test
    public void setPhone() {
        String phone="1234";
    }

    @Test
    public void setOpenHours() {
        String openHours="12:30";
    }

    @Test
    public void setSalonId() {
        String salonId="1111";
    }
}