<?php

function getHostRoot($file)
{
	$file_path = realpath($file);
	$pos_of_ez = strpos($file_path, "ez.");
	$pos_of_ez = strpos($file_path, "\\", ($pos_of_ez + 1) );
	if ( $pos_of_ez < 1)
	{
		$pos_of_ez = strlen($file_path);
	}

	return substr($file_path, 0, $pos_of_ez);
}

//capture the session pasted in and the current session.
$sConnSession = $_POST["ConnSession"];
$sSession = $_SESSION['SID'];

if ($sSession == "")
{
// 	session_save_path(getHostRoot(".") . "/cgi-bin/tmp");
    session_start();
    $sSession = session_id();
    $_SESSION['SID'] =  $sSession;
}

//write the session back out so the app can keep tracking it.
print "ConnSession:"  . $sSession;
print "\n";

//connect to a DSN with a user and password
$db_name = $_POST["DSN"];

$dbq = getHostRoot(".") . "\\db\\" . $db_name . ".mdb";
$dbDriver = 'DRIVER={Microsoft Access Driver (*.mdb)}; DBQ=' . $dbq;
$connDB = odbc_connect($dbDriver, "ADODB.Connection", "password", "SQL_CUR_USE_ODBC");

$sSQL = stripslashes($_POST["SQL"]);

if (strtolower(substr($sSQL,0,7)) == "connect")
{
    $result = @odbc_exec($connDB,"select true");
    $sError = odbc_error();
    if ($sError == "")
    {
	    print "connected";
    }
    else
    {
        print "An [HttpJdbc] error has occurred." . "\n";
        print "   SQL State:" . $sError . "\n";
        print " Description:" . odbc_errormsg($connDB) . "\n";
        print "         SQL:" . $sSQL . "\n";
    }
}
else if (strtolower(substr($sSQL,0,10)) == "disconnect")
{
    //close the connection
    odbc_close($connDB);
}
else if (strtolower(substr($sSQL,0,6)) == "select")
{
    //perform the query
    $result = @odbc_exec($connDB, $sSQL);
    $sError = odbc_error();
    if ($sError == "")
    {
        //field names & types
        $colCount = odbc_num_fields($result);
        $colType = array();
        for ($i=1; $i<= $colCount; $i++)
        {
            $colType[$i] = "NULL";
        }
        //fetch the data from the database
        while(odbc_fetch_row($result))
        {
            $iFields = odbc_num_fields($result);
            for($i=1;$i<=$iFields;$i++)
            {
                $result_value = odbc_result($result,$i);
                if ($result_value == null)
                {
                    print  "зззз" . "џ";
                }
                else
                {
                    print $result_value . "џ";

                    if ($colType[$i] != odbc_field_type($result, $i))
                    {
                        $colType[$i] = odbc_field_type($result, $i);
                    }
                }
            }
            print "ў";
        }

        print "\n";
        print "#@_METADATA_@#";
        //field names & types
        for ($i=1; $i<= $colCount; $i++)
        {
            print odbc_field_name($result, $i ) . "=" . $colType[$i] . "џ";
        }
    }
    else
    {
        print "An [HttpJdbc] error has occurred." . "\n";
        print "   SQL State:" . $sError . "\n";
        print " Description:" . odbc_errormsg($connDB) . "\n";
        print "         SQL:" . $sSQL . "\n";
    }
}
else
{
    // Execute my update, insert, or delete
    $result = @odbc_exec($connDB, $sSQL);
    $sError = odbc_error();
    if ($sError == "")
    {
        print odbc_num_rows($result);
    }
    else
    {
        print "An [HttpJdbc] error has occurred." . "\n";
        print "   SQL State:" . $sError . "\n";
        print " Description:" . odbc_errormsg($connDB) . "\n";
        print "         SQL:" . $sSQL . "\n";
    }
}
?>