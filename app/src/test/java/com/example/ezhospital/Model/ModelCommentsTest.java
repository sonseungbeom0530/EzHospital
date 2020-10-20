package com.example.ezhospital.Model;

import android.view.Display;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelCommentsTest {

    private String cId="cId";
    private String comment="comment";
    private String timeStamp="timeStamp";
    private String uid="uid";
    private String uEmail="uEmail";
    private String uDp="uDp";
    private String uName="uName";

    ModelComments modelComments=new ModelComments(cId,comment,timeStamp,uid,uEmail,uDp,uName);


    @Before
    public void setUp() throws Exception {
        ModelComments modelComments=new ModelComments();
    }

    @Test
    public void getcId() {
        assertEquals(modelComments.getcId(),"cId");
    }

    @Test
    public void setcId() {
        String cId="cId";
    }

    @Test
    public void getComment() {
        assertEquals(modelComments.getComment(),"comment");

    }

    @Test
    public void setComment() {
        String comment="comment";
    }

    @Test
    public void getTimeStamp() {
        assertEquals(modelComments.getTimeStamp(),"timeStamp");

    }

    @Test
    public void setTimeStamp() {
        String timeStamp="timeStamp";
    }

    @Test
    public void getUid() {
        assertEquals(modelComments.getUid(),"uid");

    }

    @Test
    public void setUid() {
        String uId="uid";
    }

    @Test
    public void getuEmail() {
        assertEquals(modelComments.getuEmail(),"uEmail");

    }

    @Test
    public void setuEmail() {
        String uEmail="uEmail";
    }

    @Test
    public void getuDp() {
        assertEquals(modelComments.getuDp(),"uDp");

    }

    @Test
    public void setuDp() {
        String uDp="uDp";
    }

    @Test
    public void getuName() {
        assertEquals(modelComments.getuName(),"uName");

    }

    @Test
    public void setuName() {
        String uName="uName";
    }
}