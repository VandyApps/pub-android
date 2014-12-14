package com.vandyapps.pubandroid;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ApplicationTest {
    
    @Test
    public void firstTest() {
        String banana = ((PubApp) Robolectric.application).getBanana();
        assertEquals(banana, "Banana");
        
    }
    
}

