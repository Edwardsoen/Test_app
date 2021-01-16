package com.example.test_app;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        Calendar c = Calendar.getInstance();
        c.setMinimalDaysInFirstWeek(7);
        c.set(2021, 0, 10);
        HashMap<Integer, Integer> main = DateClass.getWeekOfMonth(32);

//        assertEquals(c.get(Calendar.WEEK_OF_MONTH), 2);
        assertEquals(main.get(2).longValue(), 1 );



    }


}

