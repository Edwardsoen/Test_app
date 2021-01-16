package com.example.test_app;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();


    @Test
    public void useAppContext() throws ParseException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Calendar c = Calendar.getInstance();
//        c.set(2020, 1, 1);
        HashMap<Integer, Integer> main = DateClass.getWeekOfMonth(32);

//        assertEquals(c.get(Calendar.WEEK_OF_MONTH), 2);
        assertEquals(main.get(8).intValue(), 1 );


    }


    @Test
    public void normal_test (){





    }
}