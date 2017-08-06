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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Yuri Cuervo
 * @version : 1.00
 */
public class HttpJdbcResultSetMetaData
        implements ResultSetMetaData
{
    private String[] sColumns = new String[0];
    private int[] iTypes = new int[0];
    private String[] sTypeNames = new String[0];


    HttpJdbcResultSetMetaData()
    {
        super();
    }


    void setColumnNames(String[] sColumns)
    {
        this.sColumns = sColumns;
    }


    void setColumnTypes(int[] iTypes)
    {
        this.iTypes = iTypes;
    }


    void setColumnTypeNames(String[] sTypeNames)
    {
        this.sTypeNames = sTypeNames;
    }


    public int getColumnCount()
            throws SQLException
    {
        return sColumns.length;
    }


    public boolean isAutoIncrement(int column)
            throws SQLException
    {
        return false;
    }


    public boolean isCaseSensitive(int column)
            throws SQLException
    {
        return false;
    }


    public boolean isSearchable(int column)
            throws SQLException
    {
        return false;
    }


    public boolean isCurrency(int column)
            throws SQLException
    {
        return false;
    }


    public int isNullable(int column)
            throws SQLException
    {
        return ResultSetMetaData.columnNoNulls;
    }


    public boolean isSigned(int column)
            throws SQLException
    {
        return false;
    }


    public int getColumnDisplaySize(int column)
            throws SQLException
    {
        return 0;
    }


    public String getColumnLabel(int column)
            throws SQLException
    {
        return null;
    }


    public String getColumnName(int column)
            throws SQLException
    {
        return sColumns[column - 1];
    }


    public int getColumnType(int column)
            throws SQLException
    {
        return iTypes[column - 1];
    }


    public String getColumnTypeName(int column)
            throws SQLException
    {
        return sTypeNames[column - 1];
    }


    public String getSchemaName(int column)
            throws SQLException
    {
        return null;
    }


    public int getPrecision(int column)
            throws SQLException
    {
        return 0;
    }


    public int getScale(int column)
            throws SQLException
    {
        return 0;
    }


    public String getTableName(int column)
            throws SQLException
    {
        return null;
    }


    public String getCatalogName(int column)
            throws SQLException
    {
        return null;
    }


    public boolean isReadOnly(int column)
            throws SQLException
    {
        return false;
    }


    public boolean isWritable(int column)
            throws SQLException
    {
        return false;
    }


    public boolean isDefinitelyWritable(int column)
            throws SQLException
    {
        return false;
    }


    public String getColumnClassName(int column)
            throws SQLException
    {
        return null;
    }


    public <T> T unwrap(Class<T> interfaceClass)
            throws SQLException
    {
        return null;
    }


    public boolean isWrapperFor(Class<?> interfaceClass)
            throws SQLException
    {
        return false;
    }
}
