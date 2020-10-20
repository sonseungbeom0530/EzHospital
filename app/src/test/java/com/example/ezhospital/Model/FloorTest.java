package com.example.ezhospital.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FloorTest {



    Floor floor=new Floor();

    @Before
    public void setUp() throws Exception {

        Floor floor=new Floor();
    }

    @Test
    public void getName() {
        assertEquals(floor.getName(),null);
    }

    @Test
    public void setName() {
        String name="floor1";
    }
}