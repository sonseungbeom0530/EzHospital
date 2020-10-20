package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelChatTest {

    private String message="message";
    private String receiver="receiver";
    private String sender="sender";
    private String timestamp="timestamp";
    private boolean isSeen=true;

    ModelChat modelChat=new ModelChat(message,receiver,sender,timestamp, isSeen);

    @Before
    public void setUp() throws Exception {
        ModelChat modelChat=new ModelChat();
    }

    @Test
    public void getMessage() {
        assertEquals(modelChat.getMessage(),"message");
    }

    @Test
    public void setMessage() {
        String message="message";
    }

    @Test
    public void getReceiver() {
        assertEquals(modelChat.getReceiver(),"receiver");
    }

    @Test
    public void setReceiver() {
        String receiver="receiver";
    }

    @Test
    public void getSender() {
        assertEquals(modelChat.getSender(),"sender");
    }

    @Test
    public void setSender() {
        String sender="sender";
    }

    @Test
    public void getTimestamp() {
        assertEquals(modelChat.getTimestamp(),"timestamp");
    }

    @Test
    public void setTimestamp() {
        String timestamp="timestamp";
    }

    @Test
    public void isSeen() {
        assertEquals(modelChat.isSeen(),true);
    }

    @Test
    public void setSeen() {
        boolean isSeen=true;
    }
}