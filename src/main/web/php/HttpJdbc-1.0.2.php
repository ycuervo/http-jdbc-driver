<?php
require_once('./classes/_ClassLoader.php');


//capture the session pasted in and the current session.
$sConnSession = $_POST["ConnSession"];
$sSession = $_SESSION['SID'];

if ($sSession == "")
{
    session_start();
    $sSession = session_id();
    $_SESSION['SID'] =  $sSession;
}

//write the session back out so the app can keep tracking it.
echo "ConnSession:"  . $sSession;
echo "\n";

//connect to a DSN with a user and password
$db_name = $_POST["DB"];
$user_name = $_POST["USER"];
$pwd = $_POST["PWD"];

$connDB = new DBConnection("localhost", $db_name, $user_name, $pwd);

$sSQL = stripslashes($_POST["SQL"]);

if (strtolower(substr($sSQL,0,7)) == "connect")
{
    $result = $connDB->executeQuery("select true");
    if ($result)
    {
	    echo "connected";
	    $result->close();
    }
    else
    {
        echo "An [HttpJdbc] error has occurred." . "\n";
        echo " Description:" . $connDB->getError() . "\n";
        echo "          DB:" . $db_name . "\n";
        echo "         USR:" . $user_name . "\n";
        echo "         PWD:" . $pwd . "\n";
        echo "         SQL:" . $sSQL . "\n";
    }
}
else if (strtolower(substr($sSQL,0,10)) == "disconnect")
{
    //close the connection
    $connDB->close();
}
else if (strtolower(substr($sSQL,0,6)) == "select")
{
    //perform the query
    $result = $connDB->executeQuery($sSQL);
    if ($result)
    {
        //field count
        $fieldCount = $connDB->getFieldCount($result);

        //fetch the data from the database
        while($row = $connDB->nextByIndex($result))
        {
            for($i=0; $i < $fieldCount; $i++)
            {
                $result_value = $row[$i];
                if ($result_value == null)
                {
                    echo  "××××" . "ÿ";
                }
                else
                {
                    echo $result_value . "ÿ";
                }
            }
            echo "þ";
        }

        echo "\n";
        echo "#@_METADATA_@#";
        //field names & types
        for ($i=0; $i < $fieldCount; $i++)
        {
            $info = $connDB->getFieldInfo($result, $i);
            $iType = $info->type;

            echo $info->name;
            echo "=";

            //map mysqli type to java.sql.Types
            if ($iType == 0 || $iType == 5)
            {
                echo 8; //java.sql.Types.DOUBLE
            }
            else if ($iType == 1 || $iType == 2)
            {
                echo 5; //java.sql.Types.SMALLINT
            }
            else if ($iType == 3 || $iType == 8 || $iType == 9)
            {
                echo 4; //java.sql.Types.INTEGER
            }
            else if ($iType == 6)
            {
                echo 0; //java.sql.Types.NULL
            }
            else if ($iType == 7 || $iType == 11 || $iType == 12 || $iType == 13 || $iType == 14)
            {
                echo 93; //java.sql.Types.TIMESTAMP
            }
            else if ($iType == 10)
            {
                echo 91; //java.sql.Types.DATE
            }
            else if ($iType == 253 || $iType == 254)
            {
                echo 12; //java.sql.Types.VARCHAR
            }
            else
            {
                //if we don't have the type defined, just return a VARCHAR so at least the string will get pulled.
                echo 12;//java.sql.Types.VARCHAR
            }

            echo "ÿ";
        }
    }
    else
    {
        echo "An [HttpJdbc] error has occurred." . "\n";
        echo " Description:" . $connDB->getError() . "\n";
        echo "         SQL:" . $sSQL . "\n";
    }
}
else
{
    // Execute my update, insert, or delete
    if($connDB->execute($sSQL))
    {
        echo $connDB->getAffectedRowsCount();
    }
    else
    {
        echo "An [HttpJdbc] error has occurred." . "\n";
        echo " Description:" . $connDB->getError() . "\n";
        echo "         SQL:" . $sSQL . "\n";
    }
}
?>