package com.ycbsystems.httpjdbc;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Test for HttpJdbcDriver
 */
public class HttpJdbcDriverTest
{
    //connection info for test
    private String domain = "www.mydomain.com";
    private String hostPath = "app";
    private String database = "db_test";
    private String username = "user_Test";
    private String password = "userTestPwd*";
    private String apiLang = "PHP";

    private Connection conn;

    @Before
    public void setUp()
            throws Exception
    {
        String sURL = "HOST=http://" + domain;
        if (hostPath != null && hostPath.length() > 0)
        {
            sURL += "/" + hostPath + ";";
        }
        else
        {
            sURL += ";";
        }
        sURL += "DB=" + database + ";";
        sURL += "USER=" + username + ";";
        sURL += "PWD=" + password + ";";
        sURL += "LANG=" + apiLang + ";";

        Class.forName(HttpJdbcDriver.class.getName());
        conn = DriverManager.getConnection(sURL);
    }

    @Test
    public void testHttpJdbcDriver()
            throws Exception
    {
        Statement st = conn.createStatement();
        ResultSet rs;

        st.executeUpdate("DROP TABLE if exists xTest");

        String sCreate = "CREATE TABLE xTest (" +
                         "  xText text NOT NULL" +
                         ", xTextNull text" +
                         ", xMemo varchar(20) NOT NULL" +
                         ", xMemoNull varchar(20) default NULL" +
                         ", xNumber int(11) NOT NULL" +
                         ", xNumberNull int(11) default NULL" +
                         ", xDate date NOT NULL" +
                         ", xDateNull date default NULL" +
                         ", xDateTime datetime NOT NULL" +
                         ", xDateTimeNull datetime default NULL" +
                         ", xCurrency decimal(19,2) NOT NULL" +
                         ", xCurrencyNull decimal(19,2) default NULL" +
                         ", xDouble double NOT NULL" +
                         ", xDoubleNull double default NULL" +
                         ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        st.executeUpdate(sCreate);

        rs = st.executeQuery("select * from xTest");

        Assert.assertFalse("unexpected record returned.", rs.next());
        rs.close();

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatTimestamp = new SimpleDateFormat("yyyy-MM-dd k:mm:ss");

        Date dt = new Date(formatDate.parse("2007-10-27 4:03:07").getTime());
        Timestamp ts = new Timestamp(formatTimestamp.parse("2008-05-17 14:30:20").getTime());

        Object arrValues[] = {"Text Value", null, "Memo Value", null, 9, null, formatDate
                .format(dt), null, formatTimestamp.format(ts), null, 1234.56, null, 4321.09, null};

        int insertCount = st.executeUpdate("insert into xTest " +
                                           "(" +
                                           "xText, xTextNull" +
                                           ",xMemo, xMemoNull" +
                                           ",xNumber, xNumberNull" +
                                           ",xDate, xDateNull" +
                                           ",xDateTime, xDateTimeNull" +
                                           ",xCurrency, xCurrencyNull" +
                                           ",xDouble, xDoubleNull" +
                                           ")" +
                                           " values " +
                                           "(" +
                                           "  '" + arrValues[0] + "' ," + arrValues[1] +
                                           ", '" + arrValues[2] + "' ," + arrValues[3] +
                                           ",  " + arrValues[4] + "  ," + arrValues[5] +
                                           ", '" + arrValues[6] + "' ," + arrValues[7] +
                                           ", '" + arrValues[8] + "' ," + arrValues[9] +
                                           ",  " + arrValues[10] + "  ," + arrValues[11] +
                                           ",  " + arrValues[12] + "  ," + arrValues[13] +
                                           ")");

        Assert.assertTrue("Could not insert record.", insertCount == 1);

        //test multiple rows
        st.executeUpdate("insert into xTest " +
                         "(" +
                         "xText, xTextNull" +
                         ",xMemo, xMemoNull" +
                         ",xNumber, xNumberNull" +
                         ",xDate, xDateNull" +
                         ",xDateTime, xDateTimeNull" +
                         ",xCurrency, xCurrencyNull" +
                         ",xDouble, xDoubleNull" +
                         ")" +
                         " values " +
                         "(" +
                         "  '" + arrValues[0] + "' ," + arrValues[1] +
                         ", '" + arrValues[2] + "' ," + arrValues[3] +
                         ",  " + arrValues[4] + "  ," + arrValues[5] +
                         ", '" + arrValues[6] + "' ," + arrValues[7] +
                         ", '" + arrValues[8] + "' ," + arrValues[9] +
                         ",  " + arrValues[10] + "  ," + arrValues[11] +
                         ",  " + arrValues[12] + "  ," + arrValues[13] +
                         ")");

        rs = st.executeQuery("select " +
                             " xText,       xTextNull" +
                             ",xMemo,       xMemoNull" +
                             ",xNumber,     xNumberNull" +
                             ",xDate,       xDateNull" +
                             ",xDateTime,   xDateTimeNull" +
                             ",xCurrency,   xCurrencyNull" +
                             ",xDouble,     xDoubleNull" +
                             " from xTest");

        int rowCount = 0;
        while (rs.next())
        {
            rowCount++;
            ResultSetMetaData meta = rs.getMetaData();
            int iCols = meta.getColumnCount();

            for (int i = 0; i < iCols; i++)
            {
                Object value1 = arrValues[i];
                Object value2;

                switch (i)
                {
                    case 0:
                    {
                        value2 = rs.getString(i + 1);

                        Assert.assertEquals("String compare", value1, value2);
                        break;
                    }
                    case 2:
                    {
                        value2 = rs.getString(i + 1);

                        Assert.assertEquals("String compare", value1, value2);
                        break;
                    }
                    case 4:
                    {
                        int int1 = Integer.valueOf(value1.toString());
                        int int2 = rs.getInt(i + 1);

                        Assert.assertEquals("Integer compare", int1, int2);
                        break;
                    }
                    case 6:
                    {
                        Date dt1 = new Date(formatDate.parse(arrValues[i].toString()).getTime());
                        Date dt2 = rs.getDate(i + 1);

                        Assert.assertEquals("Date compare", dt1, dt2);

                        break;
                    }
                    case 8:
                    {
                        Timestamp dt1 = new Timestamp(formatTimestamp.parse(arrValues[i].toString()).getTime());
                        Timestamp dt2 = rs.getTimestamp(i + 1);

                        Assert.assertEquals("Timestamp compare", dt1, dt2);
                        break;
                    }
                    case 10:
                    {
                        double d1 = Double.valueOf(value1.toString());
                        double d2 = rs.getDouble(i + 1);

                        Assert.assertEquals("Double compare", d1, d2);
                        break;
                    }
                    case 12:
                    {
                        double d1 = Double.valueOf(value1.toString());
                        double d2 = rs.getDouble(i + 1);

                        Assert.assertEquals("Double compare", d1, d2);
                        break;
                    }
                    default:
                    {
                        value2 = rs.getObject(i + 1);

                        Assert.assertEquals("Object compare", value1, value2);
                        break;
                    }
                }
            }
        }

        Assert.assertTrue("ROW COUNT", rowCount == 2);
    }
}
