<?php
function __autoload($class_name)
{
	//class paths
    $dirPaths = array('./classes/',
    		          '../classes/',
    		          '/classes/');

	//for each directory
    foreach($dirPaths as $dir)
    {
    	//see if the file exists
        if(file_exists($dir.$class_name . '.php'))
        {
        	require_once($dir.$class_name . '.php');

        	return;
        }
	}
}
?>