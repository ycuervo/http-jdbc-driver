package com.ycbsystems.util;

import com.ycbsystems.text.YCBFormats;
import com.ycbsystems.type.DatabaseType;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

/**
 * User: Yuri Cuervo
 * Date: Jul 28, 2007
 * Time: 5:32:35 PM
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class YCBUtils
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();


    public static String wrap(String tableName, DatabaseType dbType)
    {
        if (dbType == DatabaseType.DERBY)
        {
            return '\"' + tableName + '\"';
        }
        else if (dbType == DatabaseType.ACCESS)
        {
            return '[' + tableName + ']';
        }
        else if (dbType == DatabaseType.MYSQL)
        {
            return '`' + tableName + '`';
        }
        else
        {
            return tableName;
        }
    }


    public static String fixSQLString(String sVal, final int length)
    {
        if (sVal == null || sVal.length() == 0)
        {
            return "";
        }
        else if (length != -1 && sVal.length() > length)
        {
            sVal = sVal.substring(0, length);
        }
        return sVal.replaceAll("\'", "\'\'").replaceAll("\"", "\"\"");
    }


    public static String getDateCompareString(Date dateString, DatabaseType dbType)
    {
        char delimiter = '\'';

        if (dbType == DatabaseType.ACCESS)
        {
            delimiter = '#';
        }

        return delimiter + YCBFormats.formatDate(dateString, dbType) + delimiter;
    }

    public static String getIsNullString(String sColumn, DatabaseType dbType)
    {
        if (dbType == DatabaseType.ACCESS)
        {
            return "isNull(" + sColumn + ")";
        }
        else
        {
            return sColumn + " is null";
        }
    }

    public static HttpURLConnection getHttpURLConnection(String path)
            throws IOException, URISyntaxException
    {
        final URI uri = new URI(path);
        final URL url = uri.toURL();

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent",
                                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " +
                                      "AppleWebKit/537.31 (KHTML, like Gecko) " +
                                      "Chrome/26.0.1410.65 " +
                                      "Safari/537.31");

        return connection;
    }

    public static String base64encode(String text)
            throws UnsupportedEncodingException
    {
        return enc.encode(text.getBytes(DEFAULT_ENCODING));
    }

    public static String base64decode(String text)
            throws IOException
    {
        return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
    }
}
