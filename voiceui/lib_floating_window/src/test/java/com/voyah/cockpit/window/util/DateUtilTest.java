package com.voyah.cockpit.window.util;

import static org.junit.Assert.assertEquals;

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
                {"2024-04-26 23:59:59", "今天"},
                {"2024-04-26 00:00:00", "今天"},
                {"2024-04-27 00:00:00", "明天"},
                {"2024-06-26 00:00:00", "周三"}
        });
    }

    @Test
    public void getCustomStr() {
//        assertEquals(mOutputDate, DateUtil.getCustomStr(mInputDate));
        assertEquals(mOutputDate, DateUtil.getCustomStr2(mInputDate));
    }
}