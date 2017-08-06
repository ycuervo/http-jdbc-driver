<?php
class DBConnection
{
	private $connection;
	
	public function __construct($host, $database, $user_name, $password)
	{
		$this->connection = new mysqli($host, $user_name, $password, $database);
	}
	
	public function close()
	{
		$this->connection->close();
	}
		
	public function execute($sql)
	{
		return $this->connection->query($sql);
	}

	public function executeQuery($sql)
	{
		return $this->connection->query($sql);
	}

	public function getError()
	{
		return mysql_error();
	}

	public function getFieldCount($result)
	{
	    return $result->field_count;
	}

	public function nextByIndex($result)
	{
	    return $result->fetch_row();
	}

	public function nextByName($result)
	{
	    return $result->fetch_assoc();
	}

	public function getFieldInfo($result, $index)
	{
	    return  $result->fetch_field_direct($index);
	}

    public function getAffectedRowsCount()
    {
        return $this->connection->affected_rows;
    }

	public function escapeString($value)
	{
		return $this->connection->real_escape_string($value);
	}
	
	public function getInsertID()
	{
		return $this->connection->insert_id;
	}
}
?>