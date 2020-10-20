package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelPostTest {

    private String pId="pId";
    private String pTitle="pTitle";
    private String pDescr="pDescr";
    private String pImage="pImage";
    private String pTime="pTime";
    private String uid="uid";
    private String uEmail="uEmail";
    private String uDp="uDp";
    private String uName="uName";
    private String pLikes="pLikes";
    private String pComments="pComments";


    ModelPost modelPost=new ModelPost(pId,pTitle,pDescr,pImage,pTime,uid,uEmail,uDp,uName,pLikes,pComments);

    @Before
    public void setUp() throws Exception {
        ModelPost modelPost=new ModelPost();
    }

    @Test
    public void getpId() {
        assertEquals(modelPost.getpId(),"pId");
    }

    @Test
    public void setpId() {
        String pId="pId";
    }

    @Test
    public void getpTitle() {
        assertEquals(modelPost.getpTitle(),"pTitle");
    }

    @Test
    public void setpTitle() {
        String pTitle="pTitle";
    }

    @Test
    public void getpDescr() {
        assertEquals(modelPost.getpDescr(),"pDescr");
    }

    @Test
    public void setpDescr() {
        String pDescr="pDescr";
    }

    @Test
    public void getpImage() {
        assertEquals(modelPost.getpImage(),"pImage");
    }

    @Test
    public void setpImage() {
        String pImage="pImage";
    }

    @Test
    public void getpTime() {
        assertEquals(modelPost.getpTime(),"pTime");
    }

    @Test
    public void setpTime() {
        String pTime="pTime";
    }

    @Test
    public void getUid() {
        assertEquals(modelPost.getUid(),"uid");
    }

    @Test
    public void setUid() {
        String uid="uid";
    }

    @Test
    public void getuEmail() {
        assertEquals(modelPost.getuEmail(),"uEmail");
    }

    @Test
    public void setuEmail() {
        String uEmail="uEmail";
    }

    @Test
    public void getuDp() {
        assertEquals(modelPost.getuDp(),"uDp");
    }

    @Test
    public void setuDp() {
        String uDp="uDp";
    }

    @Test
    public void getuName() {
        assertEquals(modelPost.getuName(),"uName");
    }

    @Test
    public void setuName() {
        String uName="uName";
    }

    @Test
    public void getpLikes() {
        assertEquals(modelPost.getpLikes(),"pLikes");
    }

    @Test
    public void setpLikes() {
        String pLikes="pLikes";
    }

    @Test
    public void getpComments() {
        assertEquals(modelPost.getpComments(),"pComments");
    }

    @Test
    public void setpComments() {
        String pComments="pComments";
    }
}