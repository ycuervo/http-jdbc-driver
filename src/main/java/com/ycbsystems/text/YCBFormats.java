package com.ycbsystems.text;


import com.ycbsystems.type.DatabaseType;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * User: Yuri Cuervo
 * Date: Jul 28, 2007
 * Time: 5:20:07 PM
 */
@SuppressWarnings("WeakerAccess")
public class YCBFormats
{
    public static final DecimalFormat formatCurr = new DecimalFormat("$###,###,##0.00");


    public static final SimpleDateFormat formatDerbyTimestamp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
    public static final SimpleDateFormat formatYYYYMMDDKKMMSS = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    public static final SimpleDateFormat formatYYYYMMDDKKMMSSS = new SimpleDateFormat("yyyy/MM/dd kk:mm.sss");
    public static final SimpleDateFormat formatMMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat formatMMDDYY = new SimpleDateFormat("MM/dd/yy");
    public static final SimpleDateFormat formatMySQLYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat formatMySQLYYMMDD = new SimpleDateFormat("yy-MM-dd");

    public static final SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("EEEE");

    public static final SimpleDateFormat formatMonthOfYear = new SimpleDateFormat("MMMM");


    public static java.util.Date parseDate(final String sDate)
            throws ParseException
    {
        return parseDate(sDate, DatabaseType.MYSQL);
    }

    public static java.util.Date parseDate(final String sDate, final DatabaseType dbType)
            throws ParseException
    {
        java.util.Date dt = null;
        if (sDate != null)
        {
            if (dbType == DatabaseType.MYSQL)
            {
                if (sDate.substring(sDate.lastIndexOf('-')).length() == 3)
                {
                    dt = formatMySQLYYMMDD.parse(sDate);
                }
                else
                {
                    dt = formatMySQLYYMMDD.parse(sDate);
                }
            }
            else
            {
                if (sDate.substring(sDate.lastIndexOf('/')).length() == 3)
                {
                    dt = formatMMDDYY.parse(sDate);
                }
                else
                {
                    dt = formatMMDDYYYY.parse(sDate);
                }
            }
        }

        return dt;
    }

    public static String formatDate(final java.util.Date date)
    {
        return formatDate(date, DatabaseType.MYSQL);
    }

    public static String formatDate(final java.util.Date date, final DatabaseType dbType)
    {
        if (dbType == DatabaseType.MYSQL)
        {
            return formatMySQLYYYYMMDD.format(date);
        }
        else
        {
            return formatMMDDYYYY.format(date);
        }
    }

    public static String formatTimestamp(final Timestamp time)
    {
        return formatTimestamp(time, DatabaseType.MYSQL);
    }

    public static String formatTimestamp(final Timestamp time, final DatabaseType dbType)
    {
        if (dbType == DatabaseType.DERBY)
        {
            return formatDerbyTimestamp.format(time);
        }
        else
        {
            return formatYYYYMMDDKKMMSSS.format(time);
        }
    }
}
