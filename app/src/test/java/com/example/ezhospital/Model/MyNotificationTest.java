package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;



public class MyNotificationTest {

    @Before
    public void setUp(){
        MyNotification myNotification=new MyNotification();
    }

    @Test
    public void setUid() {
        String uid="uid";
    }

    @Test
    public void setTitle() {
        String title="title";
    }

    @Test
    public void setContent() {
        String content="content";
    }

    @Test
    public void setRead() {
        boolean read=true;
    }
}