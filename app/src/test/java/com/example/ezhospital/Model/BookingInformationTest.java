package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import io.paperdb.Book;

import static org.junit.Assert.*;

public class BookingInformationTest {


    private String customerName="Tom";
    private String customerPhone="1234";
    private String time="12:30";
    private String barberId="0001";
    private String barberName="David";
    private String salonId="0001";
    private String salonAddress="salonAddress";
    private String salonName="salonName";

    private Long slot= Long.valueOf(0);

    BookingInformation bookingInformation=new BookingInformation(customerName,customerPhone,time,barberId,barberName,salonId,salonName,salonAddress,slot);

    @Before
    public void setUp(){
        BookingInformation bookingInformation=new BookingInformation();
    }
    @Test
    public void getCustomerName() {
        assertEquals(bookingInformation.getCustomerName(),"Tom");
    }

    @Test
    public void setCustomerName() {
        String customerName="Tom";
    }

    @Test
    public void getCustomerPhone() {
        assertEquals(bookingInformation.getCustomerPhone(),"1234");
    }

    @Test
    public void setCustomerPhone() {
        String customerPhone="1234";
    }

    @Test
    public void getTime() {
        assertEquals(bookingInformation.getTime(),"12:30");
    }

    @Test
    public void setTime() {
        String time="12:30";
    }

    @Test
    public void getBarberId() {
        assertEquals(bookingInformation.getBarberId(),"0001");
    }

    @Test
    public void setBarberId() {
        String barberId="0001";
    }

    @Test
    public void getBarberName() {
        assertEquals(bookingInformation.getBarberName(),"David");
    }

    @Test
    public void setBarberName() {
       String  barberName="David";
    }

    @Test
    public void getSalonId() {
        assertEquals(bookingInformation.getSalonId(),"0001");
    }

    @Test
    public void setSalonId() {
        String salonId="0001";
    }

    @Test
    public void getSalonName() {
        assertEquals(bookingInformation.getSalonName(),"salonName");
    }

    @Test
    public void setSalonName() {
    }

    @Test
    public void getSalonAddress() {
        assertEquals(bookingInformation.getSalonAddress(),"salonAddress");
    }

    @Test
    public void setSalonAddress() {
       String salonAddress="salonAddress";
    }


    @Test
    public void getSlot() {
        assertEquals(bookingInformation.getSlot(),Long.valueOf(0));
    }

    @Test
    public void setSlot() {
        Long slot= Long.valueOf(0);
    }



}