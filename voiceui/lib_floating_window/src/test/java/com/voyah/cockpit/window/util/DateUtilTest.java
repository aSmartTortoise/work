package com.voyah.cockpit.window.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * author : jie wang
 * date : 2024/4/9 15:59
 * description :
 */
@RunWith(Parameterized.class)
public class DateUtilTest {

    private final String mInputDate;
    private final String mOutputDate;

    public DateUtilTest(String inputDate, String outputDate) {
        this.mInputDate = inputDate;
        this.mOutputDate = outputDate;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }


    @Parameterized.Parameters
    public static Collection paramsCollection() {
        return Arrays.asList(new Object[][] {
                {"2024-12-05 23:59:59", "今天"},
                {"2024-12-05 00:00:00", "今天"},
                {"2024-12-06 00:00:00", "明天"},
                {"2024-12-28 00:00:00", "周六"}
        });
    }

    @Test
    public void getCustomStr() {
//        assertEquals(mOutputDate, DateUtil.getCustomStr(mInputDate));
        String timeStr = "2024-06-27 23:59:59";
        long timeStamp = DateUtil.getTimeStamp(timeStr);
        int day = DateUtil.getDay(timeStamp);
        int month = DateUtil.getMonth(timeStamp) + 1;
        String customTimeStr = DateUtil.getCustomTimeStr(timeStamp, false);
        System.out.println("customTimeStr:" + customTimeStr);
        assertEquals(mOutputDate, DateUtil.getCustomStr2(mInputDate));

    }

    @Test
    public void isToday() {
        assertTrue(DateUtil.isToday("2024-12-05 00:00:00"));
    }
}