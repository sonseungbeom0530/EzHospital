package com.example.ezhospital.Model;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelUserTest {

    private String name="name";
    private String email="email@com";
    private String search="search";
    private String image="image";
    private String phone="phone";
    private String uid="uid";
    private String cover="cover";
    private String typingTo="typingTo";

    ModelUser modelUser=new ModelUser(name,email,search,phone,image,cover,uid,typingTo);


    @Before
    public void setUp(){
        ModelUser modelUser=new ModelUser();
    }

    @Test
    public void getName() {
        assertEquals(modelUser.getName(),"name");
    }

    @Test
    public void setName() {
        String name="name";
    }

    @Test
    public void getEmail() {
        assertEquals(modelUser.getEmail(),"email@com");
    }

    @Test
    public void setEmail() {
        String email="email@com";
    }

    @Test
    public void getSearch() {
        assertEquals(modelUser.getSearch(),"search");
    }

    @Test
    public void setSearch() {
        String search="search";
    }

    @Test
    public void getPhone() {
        assertEquals(modelUser.getPhone(),"phone");

    }

    @Test
    public void setPhone() {
        String phone="phone";
    }

    @Test
    public void getImage() {
        assertEquals(modelUser.getImage(),"image");
    }

    @Test
    public void setImage() {
        String image="image";
    }

    @Test
    public void getCover() {
        assertEquals(modelUser.getCover(),"cover");
    }

    @Test
    public void setCover() {
        String cover="cover";
    }

    @Test
    public void getUid() {
        assertEquals(modelUser.getUid(),"uid");
    }

    @Test
    public void setUid() {
        String uid="uid";
    }

    @Test
    public void getTypingTo() {
        assertEquals(modelUser.getTypingTo(),"typingTo");
    }

    @Test
    public void setTypingTo() {
        String typingTo="typingTo";
    }
}