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

import com.ycbsystems.text.YCBFormats;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Yuri Cuervo
 * @version : 1.0
 */
public class AspJdbcStatement
        implements java.sql.Statement
{
    private AspJdbcConnection connDB;

    private SimpleDateFormat formatDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    AspJdbcStatement(AspJdbcConnection connDB)
    {
        this.connDB = connDB;
    }


    public Connection getConnection()
            throws
            SQLException
    {
        return connDB;
    }


    public ResultSet executeQuery(String sql)
            throws
            SQLException
    {
        HttpJdbcResultSet result = new HttpJdbcResultSet();

        BufferedReader in = null;
        try
        {
            in = connDB.getDataStream(sql);

            StringBuilder sbData = new StringBuilder();

            String sData = in.readLine();

            if (sData == null || sData.equals(HttpJdbcDriver.ERROR_TAG))
            {
                StringBuilder sError = new StringBuilder(sData + '\n');

                while ((sData = in.readLine()) != null)
                {
                    sError.append(sData).append('\n');
                }

                throw new SQLException(sError.toString());
            }

            if (sbData.length() > 0)
            {
                sbData.deleteCharAt(sbData.length() - 1);
            }

            do
            {
                //is this the metadata row
                if (sData.startsWith(HttpJdbcDriver.META_DATA_TAG))
                {
                    if (sbData.length() > 0)
                    {
                        sbData.deleteCharAt(sbData.length() - 1);
                    }
                    setMetaData(sData, result);
                }
                else
                {
                    sbData.append(sData).append("\n");
                }
                sData = in.readLine();
            }
            while (sData != null);

            if (sbData.length() > 0)
            {
                sbData.deleteCharAt(sbData.length() - 1);
            }

            String sDataLines = sbData.toString();
            StringTokenizer tokRows = new StringTokenizer(sDataLines, HttpJdbcDriver.getDelimiterRow());

            while (tokRows.hasMoreTokens())
            {
                String sRowOfData = tokRows.nextToken();

                List<Object> vctRow = new ArrayList<>();

                while (sRowOfData.length() > 0)
                {
                    int idx = sRowOfData.indexOf(HttpJdbcDriver.getDelimiterCol());

                    int iColType = result.getColumnType(vctRow.size() + 1);
                    String sColValue = sRowOfData.substring(0, idx);

                    if (sColValue.equals("××××"))
                    {
                        vctRow.add(null);
                    }
                    else
                    {
                        Object objColVal = getValueByType(sColValue, iColType);
                        vctRow.add(objColVal);
                    }

                    sRowOfData = sRowOfData.substring(idx + 1);
                }
                result.addRow(vctRow);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private void setMetaData(String sData, HttpJdbcResultSet result)
    {
        //first line of data should be the column names & types
        StringTokenizer tokenizer = new StringTokenizer(sData.substring(HttpJdbcDriver.META_DATA_TAG.length()),
                                                        HttpJdbcDriver.getDelimiterCol());

        int iColumnCount = tokenizer.countTokens();
        String sColumns[] = new String[iColumnCount];
        int iTypes[] = new int[iColumnCount];
        String sTypeNames[] = new String[iColumnCount];

        for (int i = 0; i < sColumns.length; i++)
        {
            String sDescription = tokenizer.nextToken();

            int idx = sDescription.indexOf("=");

            sColumns[i] = sDescription.substring(0, idx);
            int iVarType = Integer.parseInt(sDescription.substring(idx + 1));
            iTypes[i] = getColumnType(iVarType);
            sTypeNames[i] = getColumnTypeName(iVarType);
        }
        result.setColumnNames(sColumns);
        result.setColumnTypes(iTypes);
        result.setColumnTypeNames(sTypeNames);
    }


    private int getColumnType(int iVarType)
    {
        if (iVarType == 0)
        {
            return Types.NULL;
        }
        else if (iVarType == 1)
        {
            return Types.NULL;
        }
        else if (iVarType == 8)
        {
            return Types.VARCHAR;
        }
        else if (iVarType == 2)
        {
            return Types.SMALLINT;
        }
        else if (iVarType == 3)
        {
            return Types.INTEGER;
        }
        else if (iVarType == 4)
        {
            return Types.FLOAT;
        }
        else if (iVarType == 5 || iVarType == 6)
        {
            return Types.DOUBLE;
        }
        else if (iVarType == 7)
        {
            return Types.TIMESTAMP;
        }
        else if (iVarType == 11)
        {
            return Types.BOOLEAN;
        }
        else
        {
            return Types.NULL;
        }
    }


    private String getColumnTypeName(int iVarType)
    {
        if (iVarType == 0 || iVarType == 1)
        {
            return "NULL";
        }
        else if (iVarType == 2 || iVarType == 3)
        {
            return "INTEGER";
        }
        else if (iVarType == 4 || iVarType == 5 || iVarType == 6)
        {
            return "DOUBLE";
        }
        else if (iVarType == 7)
        {
            return "TIMESTAMP";
        }
        else if (iVarType == 8)
        {
            return "VARCHAR";
        }
        else if (iVarType == 9 || iVarType == 12 || iVarType == 13)
        {
            return "BINARY";
        }
        else if (iVarType == 11)
        {
            return "BOOLEAN";
        }
        else if (iVarType == 17)
        {
            return "TINYINT";
        }
        else
        {
            return "TYPE NOT SUPPORTED";
        }
    }

    private Object getValueByType(String sVal, int iType)
    {
        if (sVal == null ||
            (sVal.length() == 0 && iType != Types.VARCHAR))
        {
            return null;
        }

        Object objRet = null;

        switch (iType)
        {
            case Types.NULL:
                //let it return null
                break;
            case Types.SMALLINT:
                objRet = Short.valueOf(sVal);
                break;
            case Types.INTEGER:
                objRet = Integer.valueOf(sVal);
                break;
            case Types.FLOAT:
                objRet = Float.valueOf(sVal);
                break;
            case Types.DOUBLE:
                objRet = Double.valueOf(sVal);
                break;
            case Types.TIMESTAMP:
                try
                {
                    if (sVal.length() > 10)
                    {
                        objRet = formatDateTime.parse(sVal);
                    }
                    else
                    {
                        objRet = YCBFormats.formatMMDDYYYY.parse(sVal);
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                break;
            case Types.VARCHAR:
                objRet = sVal;
                break;
            case Types.BOOLEAN:
                objRet = sVal.equalsIgnoreCase("t") ||
                         sVal.equalsIgnoreCase("true") ||
                         sVal.equalsIgnoreCase("y") ||
                         sVal.equalsIgnoreCase("1");
                break;
        }

        if (objRet == null)
        {
            System.out.println("AspJdbc getValue failed with Val=" + sVal + " Type=" + iType);
        }

        return objRet;
    }


    public int executeUpdate(String sql)
            throws
            SQLException
    {
        int iAffected = 0;
        BufferedReader in = null;
        try
        {
            in = connDB.getDataStream(sql);

            String sData = in.readLine();

            if (sData != null && !sData.equals(HttpJdbcDriver.ERROR_TAG))
            {
                iAffected = Integer.parseInt(sData);
            }
            else
            {
                StringBuilder sError = new StringBuilder(sData + '\n');

                while ((sData = in.readLine()) != null)
                {
                    sError.append(sData).append('\n');
                }

                throw new SQLException(sError.toString());
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
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

        return iAffected;
    }


    public void close()
            throws
            SQLException
    {
        BufferedReader in = null;
        try
        {
            in = connDB.getDataStream("disconnect");
        }
        catch (Throwable e)
        {
            throw new SQLException(e.getMessage());
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
    }


    public int getMaxFieldSize()
            throws
            SQLException
    {
        return 0;
    }


    public void setMaxFieldSize(int max)
            throws
            SQLException
    {
    }


    public int getMaxRows()
            throws
            SQLException
    {
        return 0;
    }


    public void setMaxRows(int max)
            throws
            SQLException
    {
    }


    public void setEscapeProcessing(boolean enable)
            throws
            SQLException
    {
    }


    public int getQueryTimeout()
            throws
            SQLException
    {
        return 0;
    }


    public void setQueryTimeout(int seconds)
            throws
            SQLException
    {
    }


    public void cancel()
            throws
            SQLException
    {
    }


    public SQLWarning getWarnings()
            throws
            SQLException
    {
        return null;
    }


    public void clearWarnings()
            throws
            SQLException
    {
    }


    public void setCursorName(String name)
            throws
            SQLException
    {
    }


    public boolean execute(String sql)
            throws
            SQLException
    {
        return false;
    }


    public ResultSet getResultSet()
            throws
            SQLException
    {
        return null;
    }


    public int getUpdateCount()
            throws
            SQLException
    {
        return 0;
    }


    public boolean getMoreResults()
            throws
            SQLException
    {
        return false;
    }


    public void setFetchDirection(int direction)
            throws
            SQLException
    {
    }


    public int getFetchDirection()
            throws
            SQLException
    {
        return ResultSet.FETCH_FORWARD;
    }


    public void setFetchSize(int rows)
            throws
            SQLException
    {
    }


    public int getFetchSize()
            throws
            SQLException
    {
        return 0;
    }


    public int getResultSetConcurrency()
            throws
            SQLException
    {
        return ResultSet.CONCUR_READ_ONLY;
    }


    public int getResultSetType()
            throws
            SQLException
    {
        return ResultSet.TYPE_SCROLL_INSENSITIVE;
    }


    public void addBatch(String sql)
            throws
            SQLException
    {
    }


    public void clearBatch()
            throws
            SQLException
    {
    }


    public int[] executeBatch()
            throws
            SQLException
    {
        return new int[0];
    }


    public boolean getMoreResults(int current)
            throws
            SQLException
    {
        return false;
    }


    public ResultSet getGeneratedKeys()
            throws
            SQLException
    {
        return null;
    }


    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws
            SQLException
    {
        return 0;
    }


    public int executeUpdate(String sql, int columnIndexes[])
            throws
            SQLException
    {
        return 0;
    }


    public int executeUpdate(String sql, String columnNames[])
            throws
            SQLException
    {
        return 0;
    }


    public boolean execute(String sql, int autoGeneratedKeys)
            throws
            SQLException
    {
        return false;
    }


    public boolean execute(String sql, int columnIndexes[])
            throws
            SQLException
    {
        return false;
    }


    public boolean execute(String sql, String columnNames[])
            throws
            SQLException
    {
        return false;
    }


    public int getResultSetHoldability()
            throws
            SQLException
    {
        return 0;
    }


    public boolean isClosed()
            throws
            SQLException
    {
        return false;
    }


    public void setPoolable(boolean poolable)
            throws
            SQLException
    {

    }


    public boolean isPoolable()
            throws
            SQLException
    {
        return false;
    }

    public void closeOnCompletion()
            throws
            SQLException
    {

    }

    public boolean isCloseOnCompletion()
            throws
            SQLException
    {
        return false;
    }


    public <T> T unwrap(Class<T> interfaceClass)
            throws
            SQLException
    {
        return null;
    }


    public boolean isWrapperFor(Class<?> interfaceClass)
            throws
            SQLException
    {
        return false;
    }
}
