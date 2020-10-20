package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelChatlistTest {

    private String id="id";

    ModelChatlist modelChatlist=new ModelChatlist(id);

    @Before
    public void setUp() throws Exception {
        ModelChatlist modelChatlist=new ModelChatlist();
    }

    @Test
    public void getId() {
        assertEquals(modelChatlist.getId(),"id");

    }

    @Test
    public void setId() {
        String id="id";
    }
}