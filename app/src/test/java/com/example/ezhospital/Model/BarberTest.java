package com.example.ezhospital.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLInput;

import static org.junit.Assert.*;

public class BarberTest  {

    private String name="David";
    private String username="aclinic_David";
    private String password="1234";
    private String barberId="1";
    private Long rating= Long.valueOf(0);

    Barber barber=new Barber();




    @Test
    public void setName() {
        String name="David";
    }



    @Test
    public void setUsername() {
        String username="aclinic_David";

    }



    @Test
    public void setPassword() {
        String password="1234";

    }



    @Test
    public void setRating() {
        Long rating= Long.valueOf(0);
    }



    @Test
    public void setBarberId() {
        String barberId="1";

    }


}