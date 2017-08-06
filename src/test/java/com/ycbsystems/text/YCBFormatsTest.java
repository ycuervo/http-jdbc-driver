package com.ycbsystems.text;

import com.ycbsystems.type.DatabaseType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

public class YCBFormatsTest
{
    private long FIXED_TIME_FOR_DATES = 1030172400000L;
    private long FIXED_TIME_FOR_TIMESTAMPS = 1030179911000L;

    @Test
    public void testParseDate()
            throws Exception
    {
        Date date = YCBFormats.parseDate("08/24/2002", DatabaseType.ACCESS);
        Assert.assertEquals("YCBFormats.parseDate('08/24/2002') for ACCESS.", FIXED_TIME_FOR_DATES, date.getTime());

        date = YCBFormats.parseDate("02-08-24", DatabaseType.MYSQL);
        Assert.assertEquals("YCBFormats.parseDate('08/24/2002') for MYSQL.", FIXED_TIME_FOR_DATES, date.getTime());

        date = YCBFormats.parseDate("08/24/2002", DatabaseType.DERBY);
        Assert.assertEquals("YCBFormats.parseDate('08/24/2002') for DERBY.", FIXED_TIME_FOR_DATES, date.getTime());
    }

    @Test
    public void testFormatDate()
            throws Exception
    {
        Date dateToFormat = new Date(FIXED_TIME_FOR_DATES);

        String date = YCBFormats.formatDate(dateToFormat, DatabaseType.ACCESS);
        Assert.assertEquals("YCBFormats.formatDate('" + dateToFormat + "') for ACCESS.", "08/24/2002", date);

        date = YCBFormats.formatDate(dateToFormat, DatabaseType.MYSQL);
        Assert.assertEquals("YCBFormats.formatDate('" + dateToFormat + "') for MYSQL.", "2002-08-24", date);

        date = YCBFormats.formatDate(dateToFormat, DatabaseType.DERBY);
        Assert.assertEquals("YCBFormats.formatDate('" + dateToFormat + "') for DERBY.", "08/24/2002", date);
    }


    @Test
    public void testFormatTimeStamp()
            throws Exception
    {
        Timestamp dateToFormat = new Timestamp(FIXED_TIME_FOR_TIMESTAMPS);

        String date = YCBFormats.formatTimestamp(dateToFormat, DatabaseType.ACCESS);
        Assert.assertEquals("YCBFormats.formatTimestamp('" + dateToFormat + "') for ACCESS.", "2002/08/24 02:05.011", date);

        date = YCBFormats.formatDate(dateToFormat, DatabaseType.MYSQL);
        Assert.assertEquals("YCBFormats.formatTimestamp('" + dateToFormat + "') for MYSQL.", "2002-08-24", date);

        date = YCBFormats.formatDate(dateToFormat, DatabaseType.DERBY);
        Assert.assertEquals("YCBFormats.formatTimestamp('" + dateToFormat + "') for DERBY.", "08/24/2002", date);
    }
}
