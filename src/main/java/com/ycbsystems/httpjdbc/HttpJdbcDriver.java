/*
*****************************************************************************
Copyright (c) 2004
Yuri Cuervo
All Rights Reserved

Programmer: Yuri Cuervo
Created: Feb 8, 2004
*****************************************************************************
*/
package com.ycbsystems.httpjdbc;

import com.ycbsystems.util.YCBUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * The Driver
 *
 * @author Yuri Cuervo
 * @version : 1.0
 */
public class HttpJdbcDriver
        implements java.sql.Driver
{
    private static final String sVersion = "1.0.3";

    static final String META_DATA_TAG = "#@_METADATA_@#";

    static final String ERROR_TAG = "An [HttpJdbc] error has occurred.";

    private static final String CHART_SET = "UTF-8";

    static
    {
        try
        {
            DriverManager.registerDriver(new HttpJdbcDriver());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    private static final char NULL_MARKER = '\u0000';
    private static final char DELIMITER_COL = '\u0001';
    private static final char DELIMITER_ROW = '\u0010';

    static Object[] getDataStream(URL connDB, String sCookie, String sSession, String sDSN, String sDB,
                                  String sUser, String sPwd, String sSQL)
            throws Throwable
    {
        final URLConnection connection = YCBUtils.getHttpURLConnection(connDB.toString());

        //Initialize connection
        connection.setDoInput(true);
        connection.setDoOutput(true);
        //make sure feed is not cashed
        connection.setUseCaches(false);

        //init. connection
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (sCookie != null)
        {
            connection.setRequestProperty("cookie", sCookie);
            connection.setRequestProperty("set-cookie", sCookie);
        }

        // get output stream
        PrintWriter out = new PrintWriter(connection.getOutputStream());

        String sPostRequest = "ConnSession=" + URLEncoder.encode(nullCheck(sSession), CHART_SET);
        sPostRequest += "&DSN=" + URLEncoder.encode(nullCheck(sDSN), CHART_SET);
        sPostRequest += "&DB=" + URLEncoder.encode(nullCheck(sDB), CHART_SET);
        sPostRequest += "&USER=" + URLEncoder.encode(nullCheck(sUser), CHART_SET);
        sPostRequest += "&PWD=" + URLEncoder.encode(nullCheck(sPwd), CHART_SET);
        sPostRequest += "&SQL=" + URLEncoder.encode(nullCheck(sSQL), CHART_SET);

        out.println(sPostRequest);

        out.close();

        final String sFileNotFound = "FILE-NOT-FOUND";
        final Object objRet[] = new Object[2];

        new Thread(() -> {
            try
            {
                objRet[1] = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHART_SET));
                objRet[0] = connection;
            }
            catch (IOException e)
            {
                if (e instanceof FileNotFoundException)
                {
                    objRet[0] = sFileNotFound;
                }
                else
                {
                    e.printStackTrace(System.err);
                }

            }
        }).start();

        try
        {
            int iWait = 40;
            while (objRet[0] == null)
            {
                Thread.sleep(500);

                if (iWait-- == 0)
                {
                    break;
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace(System.err);
        }

        if (objRet[0] == null || objRet[0].equals(sFileNotFound))
        {
            throw new SQLException("Connection failed.");
        }

        return objRet;
    }

    private static String nullCheck(String value)
    {
        return value == null ? "" : value;
    }

    static String getNullMarker()
    {
        return String.valueOf(NULL_MARKER);
    }

    static String getDelimiterCol()
    {
        return String.valueOf(DELIMITER_COL);
    }

    static String getDelimiterRow()
    {
        return String.valueOf(DELIMITER_ROW);
    }

    public Connection connect(String url, Properties info)
            throws SQLException
    {
        URLConnection connection;
        BufferedReader in = null;

        Connection connDB = null;
        try
        {
            String sHost = "";
            String sDsn = "";
            String sLang = "";
            String sDB = "";
            String sUser = "";
            String sPwd = "";

            StringTokenizer urlTokens = new StringTokenizer(url, ";", false);
            while (urlTokens.hasMoreTokens())
            {
                String token = urlTokens.nextToken();
                if (token.toUpperCase().startsWith("HOST="))
                {
                    sHost = token.substring(5);
                }
                else if (token.toUpperCase().startsWith("DSN="))
                {
                    sDsn = token.substring(4);
                }
                else if (token.toUpperCase().startsWith("LANG="))
                {
                    sLang = token.substring(5);
                }
                else if (token.toUpperCase().startsWith("DB="))
                {
                    sDB = token.substring(3);
                }
                else if (token.toUpperCase().startsWith("USER="))
                {
                    sUser = token.substring(5);
                }
                else if (token.toUpperCase().startsWith("USR="))
                {
                    sUser = token.substring(4);
                }
                else if (token.toUpperCase().startsWith("PWD="))
                {
                    sPwd = token.substring(4);
                }
            }

            URL path;
            switch (sLang)
            {
                case "PHP":
                    path = new URL(sHost + "/HttpJdbc-" + sVersion + ".php");
                    break;
                case "ASP":
                    //stopped supporting ASP... can only go up to version 1.0.1
                    path = new URL(sHost + "/HttpJdbc-1.0.1.asp");
                    break;
                default:
                    throw new SQLException("No URL found for Driver");
            }


            final Object objRet[] = getDataStream(path, null, null, sDsn, sDB, sUser, sPwd, "connect");

            if (objRet[0] == null)
            {
                throw new SQLException("Connection failed.");
            }

            connection = (URLConnection) objRet[0];
            in = (BufferedReader) objRet[1];

            if (sLang.equals("ASP"))
            {
                connDB = new AspJdbcConnection(path, sDsn, connection, in);
            }
            else if (sLang.equals("PHP"))
            {
                connDB = new PhpJdbcConnection(path, sDsn, sDB, sUser, sPwd, connection, in);
            }
        }
        catch (Throwable e)
        {
            String sMessage = e.getMessage();

            if (sMessage != null && !sMessage.contains("Server returned"))
            {
                SQLException exc = new SQLException(" Cannot connect to " + url + "\n" + sMessage);
                exc.setStackTrace(e.getStackTrace());
                throw exc;
            }
            else
            {
                e.printStackTrace();
            }
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return connDB;
    }


    public boolean acceptsURL(String url)
            throws SQLException
    {
        url = url.toUpperCase();

        boolean bAcceptURL = (url.indexOf("HOST=HTTP") == 0 &&
                              url.contains("DSN=") &&
                              url.contains("LANG="));

        if (!bAcceptURL)
        {
            throw new SQLException("Invalid connection string " + url);
        }

        return true;
    }


    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
            throws SQLException
    {
        return new DriverPropertyInfo[0];
    }


    public int getMajorVersion()
    {
        return 1;
    }


    public int getMinorVersion()
    {
        return 0;
    }


    public boolean jdbcCompliant()
    {
        return false;
    }

    @Override
    public Logger getParentLogger()
            throws
            SQLFeatureNotSupportedException
    {
        return null;
    }
}
