package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BannerTest {

    private String image="image.png";

    Banner banner=new Banner(image);

    @Before
    public void setUp(){
        Banner banner=new Banner();
    }
    @Test
    public void setImage() {
        String image="image.png";
    }
    @Test
    public void getImage() {
        assertEquals(banner.getImage(),"image.png");
    }



}