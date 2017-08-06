package com.ycbsystems.httpjdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * AspJdbc Connection.
 *
 * @author Yuri Cuervo
 * @version : 1.0
 */
public class PhpJdbcConnection
        implements java.sql.Connection
{
    private URL connDB;
    private String sDSN;
    private String sDB;
    private String sUser;
    private String sPwd;
    private String sCookie;
    private String sSession;

    private List<Statement> vctStatements = new ArrayList<>();

    PhpJdbcConnection(URL path, String sDSN, String sDB, String sUser, String sPwd, URLConnection connection,
                      BufferedReader in)
            throws
            SQLException
    {
        connDB = path;
        this.sDSN = sDSN;
        this.sDB = sDB;
        this.sUser = sUser;
        this.sPwd = sPwd;

        try
        {
            sCookie = connection.getHeaderField("set-cookie");
            if (readSessionID(in))
            {
                String sLine = in.readLine();
                if (sLine.indexOf("connected") != 0)
                {
                    StringBuilder sError = new StringBuilder(sLine + '\n');

                    while ((sLine = in.readLine()) != null)
                    {
                        sError.append(sLine).append('\n');
                    }

                    throw new SQLException(sError.toString());
                }
            }
        }
        catch (IOException e)
        {
            throw new SQLException("Connection failed.\n\t\t" + e.getMessage());
        }
    }


    private boolean readSessionID(BufferedReader in)
            throws
            IOException
    {
        String sLine = in.readLine();

        if (sLine != null)
        {
            if (sLine.indexOf("ConnSession:") == 0)
            {
                sSession = sLine.substring(12);
            }
            return true;
        }
        return false;
    }


    public Statement createStatement()
            throws
            SQLException
    {
        Statement stNew = new PhpJdbcStatement(this);
        vctStatements.add(stNew);
        return stNew;
    }


    public void close()
            throws
            SQLException
    {
        if (vctStatements != null)
        {
            for (Statement st : vctStatements)
            {
                st.close();
            }
            vctStatements.clear();
        }
    }


    BufferedReader getDataStream(final String sSQL)
            throws
            Throwable
    {
        final Object objRet[] = HttpJdbcDriver.getDataStream(connDB, sCookie, sSession, sDSN, sDB, sUser, sPwd, sSQL);

        if (objRet[0] == null)
        {
            throw new SQLException("Connection failed.");
        }

        URLConnection connection = (URLConnection) objRet[0];
        BufferedReader in = (BufferedReader) objRet[1];

        String sCookieCheck = connection.getHeaderField("set-cookie");
        if (sCookieCheck != null)
        {
            sCookie = sCookieCheck;
        }

        if (readSessionID(in))
        {
            return in;
        }

        return null;
    }


    public boolean isClosed()
            throws
            SQLException
    {
        return false;
    }


    public PreparedStatement prepareStatement(String sql)
            throws
            SQLException
    {
        return null;
    }


    public CallableStatement prepareCall(String sql)
            throws
            SQLException
    {
        return null;
    }


    public String nativeSQL(String sql)
            throws
            SQLException
    {
        return null;
    }


    public void setAutoCommit(boolean autoCommit)
            throws
            SQLException
    {
    }


    public boolean getAutoCommit()
            throws
            SQLException
    {
        return false;
    }


    public void commit()
            throws
            SQLException
    {
    }


    public void rollback()
            throws
            SQLException
    {
    }


    public DatabaseMetaData getMetaData()
            throws
            SQLException
    {
        return null;
    }


    public void setReadOnly(boolean readOnly)
            throws
            SQLException
    {
    }


    public boolean isReadOnly()
            throws
            SQLException
    {
        return false;
    }


    public void setCatalog(String catalog)
            throws
            SQLException
    {
    }


    public String getCatalog()
            throws
            SQLException
    {
        return null;
    }


    public void setTransactionIsolation(int level)
            throws
            SQLException
    {
    }


    public int getTransactionIsolation()
            throws
            SQLException
    {
        return Connection.TRANSACTION_NONE;
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


    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws
            SQLException
    {
        //todo for now return a regular connection
        return createStatement();
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws
            SQLException
    {
        return null;
    }


    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws
            SQLException
    {
        return null;
    }


    public Map<String, Class<?>> getTypeMap()
            throws
            SQLException
    {
        return null;
    }


    public void setTypeMap(Map map)
            throws
            SQLException
    {
    }


    public void setHoldability(int holdability)
            throws
            SQLException
    {
    }


    public int getHoldability()
            throws
            SQLException
    {
        return 0;
    }


    public Savepoint setSavepoint()
            throws
            SQLException
    {
        return null;
    }


    public Savepoint setSavepoint(String name)
            throws
            SQLException
    {
        return null;
    }


    public void rollback(Savepoint savepoint)
            throws
            SQLException
    {
    }


    public void releaseSavepoint(Savepoint savepoint)
            throws
            SQLException
    {
    }


    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws
            SQLException
    {
        return null;
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability)
            throws
            SQLException
    {
        return null;
    }


    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability)
            throws
            SQLException
    {
        return null;
    }


    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws
            SQLException
    {
        return null;
    }


    public PreparedStatement prepareStatement(String sql, int columnIndexes[])
            throws
            SQLException
    {
        return null;
    }


    public PreparedStatement prepareStatement(String sql, String columnNames[])
            throws
            SQLException
    {
        return null;
    }


    public Clob createClob()
            throws
            SQLException
    {
        return null;
    }


    public Blob createBlob()
            throws
            SQLException
    {
        return null;  //Todo
    }


    public NClob createNClob()
            throws
            SQLException
    {
        return null;  //Todo
    }


    public SQLXML createSQLXML()
            throws
            SQLException
    {
        return null;  //Todo
    }


    public boolean isValid(int timeout)
            throws
            SQLException
    {
        return false;  //Todo
    }


    public void setClientInfo(String name, String value)
            throws
            SQLClientInfoException
    {
        //Todo
    }


    public void setClientInfo(Properties properties)
            throws
            SQLClientInfoException
    {
        //Todo
    }


    public String getClientInfo(String name)
            throws
            SQLException
    {
        return null;  //Todo
    }


    public Properties getClientInfo()
            throws
            SQLException
    {
        return null;  //Todo
    }


    public Array createArrayOf(String typeName, Object[] elements)
            throws
            SQLException
    {
        return null;  //Todo
    }


    public Struct createStruct(String typeName, Object[] attributes)
            throws
            SQLException
    {
        return null;  //Todo
    }

    public void setSchema(String schema)
            throws
            SQLException
    {

    }

    public String getSchema()
            throws
            SQLException
    {
        return null;
    }

    public void abort(Executor executor)
            throws
            SQLException
    {

    }

    public void setNetworkTimeout(Executor executor, int milliseconds)
            throws
            SQLException
    {

    }

    public int getNetworkTimeout()
            throws
            SQLException
    {
        return 0;
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
