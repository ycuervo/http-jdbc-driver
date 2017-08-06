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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Yuri Cuervo
 * @version : 1.0
 */
public class HttpJdbcResultSet
        implements java.sql.ResultSet
{
    private int iCurrentRow = -1;
    private List<List<Object>> rowsList = new ArrayList<>();
    private HttpJdbcResultSetMetaData metaData = new HttpJdbcResultSetMetaData();
    private boolean bPrintNotImplemented = true;

    private SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat formatDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    HttpJdbcResultSet()
    {
        super();
    }


    @SuppressWarnings("unused")
    public void clear()
    {
        rowsList.clear();
        iCurrentRow = -1;
    }


    void addRow(List<Object> vctRow)
    {
        rowsList.add(vctRow);
    }


    void setColumnNames(String[] sColumns)
    {
        metaData.setColumnNames(sColumns);
    }


    void setColumnTypes(int[] iTypes)
    {
        metaData.setColumnTypes(iTypes);
    }


    void setColumnTypeNames(String[] sTypes)
    {
        metaData.setColumnTypeNames(sTypes);
    }


    public boolean next()
            throws
            SQLException
    {
        return (++iCurrentRow < rowsList.size());
    }


    public boolean previous()
            throws
            SQLException
    {
        return (--iCurrentRow > -1);
    }


    public void close()
            throws
            SQLException
    {
        iCurrentRow = -1;
        rowsList.clear();
        metaData.setColumnNames(new String[0]);
    }


    public boolean wasNull()
            throws
            SQLException
    {
        return false;
    }


    public Object getObject(int columnIndex)
            throws
            SQLException
    {
        List<Object> vctRow = rowsList.get(iCurrentRow);

        return vctRow.get(columnIndex - 1);
    }


    public String getString(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? oVal.toString() : null;
    }


    public boolean getBoolean(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null && Boolean.valueOf(oVal.toString());
    }


    public byte getByte(int columnIndex)
            throws
            SQLException
    {
        String sVal = getString(columnIndex);
        return Byte.parseByte(sVal);
    }


    public short getShort(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? Short.parseShort(oVal.toString()) : -1;
    }


    public int getInt(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? Integer.parseInt(oVal.toString()) : -1;
    }


    public long getLong(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? Long.parseLong(oVal.toString()) : -1;
    }


    public float getFloat(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? Float.parseFloat(oVal.toString()) : -1;
    }


    public double getDouble(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? Double.parseDouble(oVal.toString()) : -1;
    }


    public BigDecimal getBigDecimal(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? ((BigDecimal) oVal) : null;
    }


    public BigDecimal getBigDecimal(int columnIndex, int scale)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? ((BigDecimal) oVal) : null;
    }


    public byte[] getBytes(int columnIndex)
            throws
            SQLException
    {
        String sVal = getString(columnIndex);
        return sVal.getBytes();
    }


    public Date getDate(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);

        if (oVal == null)
        {
            return null;
        }
        else
        {
            if (oVal instanceof java.util.Date)
            {
                return new java.sql.Date(((java.util.Date) oVal).getTime());
            }
            else
            {
                if (getColumnType(columnIndex) == Types.DATE)
                {
                    try
                    {
                        return new Date(formatDate.parse(oVal.toString()).getTime());
                    }
                    catch (ParseException e)
                    {
                        return null;
                    }
                }
                return null;
            }
        }
    }


    public Time getTime(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        return oVal != null ? ((Time) oVal) : null;
    }


    public Timestamp getTimestamp(int columnIndex)
            throws
            SQLException
    {
        Object oVal = getObject(columnIndex);
        if (oVal != null)
        {
            if (oVal instanceof java.util.Date)
            {
                return new Timestamp(((java.util.Date) oVal).getTime());
            }
            else
            {
                if (getColumnType(columnIndex) == Types.TIMESTAMP)
                {
                    try
                    {
                        return new Timestamp(formatDateTime.parse(oVal.toString()).getTime());
                    }
                    catch (ParseException e)
                    {
                        return null;
                    }
                }
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    public InputStream getAsciiStream(int columnIndex)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public InputStream getUnicodeStream(int columnIndex)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public InputStream getBinaryStream(int columnIndex)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public Object getObject(String columnName)
            throws
            SQLException
    {
        return getObject(findColumn(columnName));
    }


    public String getString(String columnName)
            throws
            SQLException
    {
        return getString(findColumn(columnName));
    }


    public boolean getBoolean(String columnName)
            throws
            SQLException
    {
        return getBoolean(findColumn(columnName));
    }


    public byte getByte(String columnName)
            throws
            SQLException
    {
        return getByte(findColumn(columnName));
    }


    public short getShort(String columnName)
            throws
            SQLException
    {
        return getShort(findColumn(columnName));
    }


    public int getInt(String columnName)
            throws
            SQLException
    {
        return getInt(findColumn(columnName));
    }


    public long getLong(String columnName)
            throws
            SQLException
    {
        return getLong(findColumn(columnName));
    }


    public float getFloat(String columnName)
            throws
            SQLException
    {
        return getFloat(findColumn(columnName));
    }


    public double getDouble(String columnName)
            throws
            SQLException
    {
        return getDouble(findColumn(columnName));
    }


    public BigDecimal getBigDecimal(String columnName)
            throws
            SQLException
    {
        return getBigDecimal(findColumn(columnName));
    }


    public BigDecimal getBigDecimal(String columnName, int scale)
            throws
            SQLException
    {
        return getBigDecimal(findColumn(columnName), scale);
    }


    public byte[] getBytes(String columnName)
            throws
            SQLException
    {
        return getBytes(findColumn(columnName));
    }


    public Date getDate(String columnName)
            throws
            SQLException
    {
        return getDate(findColumn(columnName));
    }


    public Time getTime(String columnName)
            throws
            SQLException
    {
        return getTime(findColumn(columnName));
    }


    public Timestamp getTimestamp(String columnName)
            throws
            SQLException
    {
        return getTimestamp(findColumn(columnName));
    }


    public InputStream getAsciiStream(String columnName)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public InputStream getUnicodeStream(String columnName)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public InputStream getBinaryStream(String columnName)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public SQLWarning getWarnings()
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public void clearWarnings()
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
    }


    public String getCursorName()
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public ResultSetMetaData getMetaData()
            throws
            SQLException
    {
        return metaData;
    }


    int getColumnType(int i)
            throws
            SQLException
    {
        return metaData.getColumnType(i);
    }


    public int findColumn(String columnName)
            throws
            SQLException
    {
        for (int i = 0; i < metaData.getColumnCount(); i++)
        {
            if (metaData.getColumnName(i + 1).equalsIgnoreCase(columnName))
            {
                return (i + 1);
            }
        }

        throw new SQLException("column not found " + columnName);
    }


    public Reader getCharacterStream(int columnIndex)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public Reader getCharacterStream(String columnName)
            throws
            SQLException
    {
        if (bPrintNotImplemented)
        {
            throw new SQLException("method not implemented");
        }
        return null;
    }


    public boolean isBeforeFirst()
            throws
            SQLException
    {
        return iCurrentRow == -1;
    }


    public boolean isAfterLast()
            throws
            SQLException
    {
        return iCurrentRow == rowsList.size();
    }


    public boolean isFirst()
            throws
            SQLException
    {
        return iCurrentRow == 0;
    }


    public boolean isLast()
            throws
            SQLException
    {
        return iCurrentRow == rowsList.size() - 1;
    }


    public void beforeFirst()
            throws
            SQLException
    {
        iCurrentRow = -1;
    }


    public void afterLast()
            throws
            SQLException
    {
        iCurrentRow = rowsList.size();
    }


    public boolean first()
            throws
            SQLException
    {
        iCurrentRow = 0;
        return rowsList.size() > 0;
    }


    public boolean last()
            throws
            SQLException
    {
        iCurrentRow = rowsList.size() - 1;
        return rowsList.size() > 0;
    }


    public int getRow()
            throws
            SQLException
    {
        return iCurrentRow;
    }


    public boolean absolute(int row)
            throws
            SQLException
    {
        return false;
    }


    public boolean relative(int rows)
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


    public int getType()
            throws
            SQLException
    {
        return TYPE_SCROLL_INSENSITIVE;
    }


    public int getConcurrency()
            throws
            SQLException
    {
        return CONCUR_READ_ONLY;
    }


    public boolean rowUpdated()
            throws
            SQLException
    {
        return false;
    }


    public boolean rowInserted()
            throws
            SQLException
    {
        return false;
    }


    public boolean rowDeleted()
            throws
            SQLException
    {
        return false;
    }


    public void updateNull(int columnIndex)
            throws
            SQLException
    {
    }


    public void updateBoolean(int columnIndex, boolean x)
            throws
            SQLException
    {
    }


    public void updateByte(int columnIndex, byte x)
            throws
            SQLException
    {
    }


    public void updateShort(int columnIndex, short x)
            throws
            SQLException
    {
    }


    public void updateInt(int columnIndex, int x)
            throws
            SQLException
    {
    }


    public void updateLong(int columnIndex, long x)
            throws
            SQLException
    {
    }


    public void updateFloat(int columnIndex, float x)
            throws
            SQLException
    {
    }


    public void updateDouble(int columnIndex, double x)
            throws
            SQLException
    {
    }


    public void updateBigDecimal(int columnIndex, BigDecimal x)
            throws
            SQLException
    {
    }


    public void updateString(int columnIndex, String x)
            throws
            SQLException
    {
    }


    public void updateBytes(int columnIndex, byte x[])
            throws
            SQLException
    {
    }


    public void updateDate(int columnIndex, Date x)
            throws
            SQLException
    {
    }


    public void updateTime(int columnIndex, Time x)
            throws
            SQLException
    {
    }


    public void updateTimestamp(int columnIndex, Timestamp x)
            throws
            SQLException
    {
    }


    public void updateAsciiStream(int columnIndex,
                                  InputStream x,
                                  int length)
            throws
            SQLException
    {
    }


    public void updateBinaryStream(int columnIndex,
                                   InputStream x,
                                   int length)
            throws
            SQLException
    {
    }


    public void updateCharacterStream(int columnIndex,
                                      Reader x,
                                      int length)
            throws
            SQLException
    {
    }


    public void updateObject(int columnIndex, Object x, int scale)
            throws
            SQLException
    {
    }


    public void updateObject(int columnIndex, Object x)
            throws
            SQLException
    {
    }


    public void updateNull(String columnName)
            throws
            SQLException
    {
    }


    public void updateBoolean(String columnName, boolean x)
            throws
            SQLException
    {
    }


    public void updateByte(String columnName, byte x)
            throws
            SQLException
    {
    }


    public void updateShort(String columnName, short x)
            throws
            SQLException
    {
    }


    public void updateInt(String columnName, int x)
            throws
            SQLException
    {
    }


    public void updateLong(String columnName, long x)
            throws
            SQLException
    {
    }


    public void updateFloat(String columnName, float x)
            throws
            SQLException
    {
    }


    public void updateDouble(String columnName, double x)
            throws
            SQLException
    {
    }


    public void updateBigDecimal(String columnName, BigDecimal x)
            throws
            SQLException
    {
    }


    public void updateString(String columnName, String x)
            throws
            SQLException
    {
    }


    public void updateBytes(String columnName, byte x[])
            throws
            SQLException
    {
    }


    public void updateDate(String columnName, Date x)
            throws
            SQLException
    {
    }


    public void updateTime(String columnName, Time x)
            throws
            SQLException
    {
    }


    public void updateTimestamp(String columnName, Timestamp x)
            throws
            SQLException
    {
    }


    public void updateAsciiStream(String columnName,
                                  InputStream x,
                                  int length)
            throws
            SQLException
    {
    }


    public void updateBinaryStream(String columnName,
                                   InputStream x,
                                   int length)
            throws
            SQLException
    {
    }


    public void updateCharacterStream(String columnName,
                                      Reader reader,
                                      int length)
            throws
            SQLException
    {
    }


    public void updateObject(String columnName, Object x, int scale)
            throws
            SQLException
    {
    }


    public void updateObject(String columnName, Object x)
            throws
            SQLException
    {
    }


    public void insertRow()
            throws
            SQLException
    {
    }


    public void updateRow()
            throws
            SQLException
    {
    }


    public void deleteRow()
            throws
            SQLException
    {
    }


    public void refreshRow()
            throws
            SQLException
    {
    }


    public void cancelRowUpdates()
            throws
            SQLException
    {
    }


    public void moveToInsertRow()
            throws
            SQLException
    {
    }


    public void moveToCurrentRow()
            throws
            SQLException
    {
    }


    public Statement getStatement()
            throws
            SQLException
    {
        return null;
    }


    public Object getObject(int i, Map map)
            throws
            SQLException
    {
        return null;
    }


    public Ref getRef(int i)
            throws
            SQLException
    {
        return null;
    }


    public Blob getBlob(int i)
            throws
            SQLException
    {
        return null;
    }


    public Clob getClob(int i)
            throws
            SQLException
    {
        return null;
    }


    public Array getArray(int i)
            throws
            SQLException
    {
        return null;
    }


    public Object getObject(String colName, Map map)
            throws
            SQLException
    {
        return null;
    }


    public Ref getRef(String colName)
            throws
            SQLException
    {
        return null;
    }


    public Blob getBlob(String colName)
            throws
            SQLException
    {
        return null;
    }


    public Clob getClob(String colName)
            throws
            SQLException
    {
        return null;
    }


    public Array getArray(String colName)
            throws
            SQLException
    {
        return null;
    }


    public Date getDate(int columnIndex, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public Date getDate(String columnName, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public Time getTime(int columnIndex, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public Time getTime(String columnName, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public Timestamp getTimestamp(int columnIndex, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public Timestamp getTimestamp(String columnName, Calendar cal)
            throws
            SQLException
    {
        return null;
    }


    public URL getURL(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public URL getURL(String columnName)
            throws
            SQLException
    {
        return null;
    }


    public void updateRef(int columnIndex, Ref x)
            throws
            SQLException
    {
    }


    public void updateRef(String columnName, Ref x)
            throws
            SQLException
    {
    }


    public void updateBlob(int columnIndex, Blob x)
            throws
            SQLException
    {
    }


    public void updateBlob(String columnName, Blob x)
            throws
            SQLException
    {
    }


    public void updateClob(int columnIndex, Clob x)
            throws
            SQLException
    {
    }


    public void updateClob(String columnName, Clob x)
            throws
            SQLException
    {
    }


    public void updateArray(int columnIndex, Array x)
            throws
            SQLException
    {
    }


    public void updateArray(String columnName, Array x)
            throws
            SQLException
    {
    }


    public RowId getRowId(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public RowId getRowId(String columnLabel)
            throws
            SQLException
    {
        return null;
    }


    public void updateRowId(int columnIndex, RowId x)
            throws
            SQLException
    {

    }


    public void updateRowId(String columnLabel, RowId x)
            throws
            SQLException
    {

    }


    public int getHoldability()
            throws
            SQLException
    {
        return CLOSE_CURSORS_AT_COMMIT;
    }


    public boolean isClosed()
            throws
            SQLException
    {
        return false;
    }


    public void updateNString(int columnIndex, String nString)
            throws
            SQLException
    {

    }


    public void updateNString(String columnLabel, String nString)
            throws
            SQLException
    {

    }


    public void updateNClob(int columnIndex, NClob nClob)
            throws
            SQLException
    {

    }


    public void updateNClob(String columnLabel, NClob nClob)
            throws
            SQLException
    {

    }


    public NClob getNClob(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public NClob getNClob(String columnLabel)
            throws
            SQLException
    {
        return null;
    }


    public SQLXML getSQLXML(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public SQLXML getSQLXML(String columnLabel)
            throws
            SQLException
    {
        return null;
    }


    public void updateSQLXML(int columnIndex, SQLXML xmlObject)
            throws
            SQLException
    {

    }


    public void updateSQLXML(String columnLabel, SQLXML xmlObject)
            throws
            SQLException
    {

    }


    public String getNString(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public String getNString(String columnLabel)
            throws
            SQLException
    {
        return null;
    }


    public Reader getNCharacterStream(int columnIndex)
            throws
            SQLException
    {
        return null;
    }


    public Reader getNCharacterStream(String columnLabel)
            throws
            SQLException
    {
        return null;
    }


    public void updateNCharacterStream(int columnIndex, Reader x, long length)
            throws
            SQLException
    {

    }


    public void updateNCharacterStream(String columnLabel, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateAsciiStream(int columnIndex, InputStream x, long length)
            throws
            SQLException
    {

    }


    public void updateBinaryStream(int columnIndex, InputStream x, long length)
            throws
            SQLException
    {

    }


    public void updateCharacterStream(int columnIndex, Reader x, long length)
            throws
            SQLException
    {

    }


    public void updateAsciiStream(String columnLabel, InputStream x, long length)
            throws
            SQLException
    {

    }


    public void updateBinaryStream(String columnLabel, InputStream x, long length)
            throws
            SQLException
    {

    }


    public void updateCharacterStream(String columnLabel, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateBlob(int columnIndex, InputStream inputStream, long length)
            throws
            SQLException
    {

    }


    public void updateBlob(String columnLabel, InputStream inputStream, long length)
            throws
            SQLException
    {

    }


    public void updateClob(int columnIndex, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateClob(String columnLabel, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateNClob(int columnIndex, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateNClob(String columnLabel, Reader reader, long length)
            throws
            SQLException
    {

    }


    public void updateNCharacterStream(int columnIndex, Reader x)
            throws
            SQLException
    {

    }


    public void updateNCharacterStream(String columnLabel, Reader reader)
            throws
            SQLException
    {

    }


    public void updateAsciiStream(int columnIndex, InputStream x)
            throws
            SQLException
    {

    }


    public void updateBinaryStream(int columnIndex, InputStream x)
            throws
            SQLException
    {

    }


    public void updateCharacterStream(int columnIndex, Reader x)
            throws
            SQLException
    {

    }


    public void updateAsciiStream(String columnLabel, InputStream x)
            throws
            SQLException
    {

    }


    public void updateBinaryStream(String columnLabel, InputStream x)
            throws
            SQLException
    {

    }


    public void updateCharacterStream(String columnLabel, Reader reader)
            throws
            SQLException
    {

    }


    public void updateBlob(int columnIndex, InputStream inputStream)
            throws
            SQLException
    {

    }


    public void updateBlob(String columnLabel, InputStream inputStream)
            throws
            SQLException
    {

    }


    public void updateClob(int columnIndex, Reader reader)
            throws
            SQLException
    {

    }


    public void updateClob(String columnLabel, Reader reader)
            throws
            SQLException
    {

    }


    public void updateNClob(int columnIndex, Reader reader)
            throws
            SQLException
    {

    }


    public void updateNClob(String columnLabel, Reader reader)
            throws
            SQLException
    {

    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(int columnIndex, Class<T> type)
            throws
            SQLException
    {
        return (T) getObject(columnIndex);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String columnLabel, Class<T> type)
            throws
            SQLException
    {
        return (T) getObject(columnLabel);
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
