package com.ycbsystems.util;

import com.ycbsystems.type.DatabaseType;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for YCBUtils
 */
public class YCBUtilsTest
{
    @Test
    public void testWrap()
            throws Exception
    {
        String wrapped = YCBUtils.wrap("invoice", DatabaseType.ACCESS);
        Assert.assertEquals("YCBUtils.wrap('invoice') for ACCESS.", "[invoice]", wrapped);

        wrapped = YCBUtils.wrap("invoice", DatabaseType.MYSQL);
        Assert.assertEquals("YCBUtils.wrap('invoice') for MYSQL.", "`invoice`", wrapped);

        wrapped = YCBUtils.wrap("invoice", DatabaseType.DERBY);
        Assert.assertEquals("YCBUtils.wrap('invoice') for DERBY.", "\"invoice\"", wrapped);
    }

    @Test
    public void testEncodeDecode()
            throws Exception
    {
        String value = "The long lost treasure of #zegawa! Where did you go?";

        String encrypted = YCBUtils.base64encode(value);

        String decrypted = YCBUtils.base64decode(encrypted);

        Assert.assertEquals("Confirm initial value was encrypted and decrypted.", value, decrypted);
    }


}
