<%
'Dimension variables
Dim sDSN			'Holds the DSN
Dim connDB 			'Holds the Database Connection Object
Dim arrMetaDType()  'Holds the resulset metadata information
Dim rsResult		'Holds the recordset for the records in the database
Dim sSQL			'Holds the SQL query for the database
Dim sSession		'Holds the SessionID
Dim sConnSession	'Holds the SessionID that this connection is working with
Dim lAffected       'Holds the record affected by an insert, update or delete
Dim iFieldCount		'Holds the number of fields to loop through in the recordset

On error resume next  'catch ODBC/SQL errors in a minute

sConnSession = Request.Form("ConnSession")
sSession = Session.SessionID

Response.write("ConnSession:"  & sSession )
Response.write(VBNewLine)


'Create an ADO connection odject
Set connDB = Session("connDB")
If (connDB = Nothing) or (connDB.State = adStateClosed) or Not(sConnSession = sSession) then
	'get the file name to connect to
	'sDSN = "DSN=" & Request.Form("DSN")

	strCon = "DRIVER={Microsoft Access Driver (*.mdb)}; DBQ=" & Server.MapPath("\db\" & Request.Form("DSN") & ".mdb") & ";"
	'strCon = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Server.MapPath("\db\" & Request.Form("DSN") & ".mdb") & ";"

    'Set an active connection to the Connection object
    Set connDB = Server.CreateObject("ADODB.Connection")

    connDB.Open strCon

    Session("connDB") = connDB
End If

'Initialise the sSQL variable with an SQL statement to query the database
sSQL = Request.Form("SQL")

If lcase(left(sSQL,7)) = "connect" then
	'Create an ADO recordset object and run test query to make sure connection is good.
    Set rsResult = Server.CreateObject("ADODB.Recordset")
    Set rsResult = connDB.Execute("select 'true' as connected")
    Do While Not rsResult.EOF
        rsResult.MoveNext
    Loop
    If connDB.Errors.Count <> 0 then
        Response.Write "An [HttpJdbc] error has occurred." & VBNewLine
        Response.write "   SQL State:" & connDB.Errors(0).sqlstate & VBNewLine
        Response.write " Description:" & connDB.Errors(0).Description  & VBNewLine
        Response.write "Native Error:" & connDB.Errors(0).NativeError & VBNewLine
        Response.write "         SQL:" & sSQL
    Else
	    Response.write("connected")

        'Reset server objects
        rsResult.Close
        Set rsResult = Nothing
    End If
Elseif lcase(left(sSQL,10)) = "disconnect" then
    connDB.Close
    Set connDB = Nothing
    Session("connDB") = Nothing
Elseif lcase(left(sSQL,6)) = "select" then
    'Create an ADO recordset object
    Set rsResult = Server.CreateObject("ADODB.Recordset")

    'Set the recordset with the SQL query
    Set rsResult = connDB.Execute(sSQL)

    If connDB.Errors.Count <> 0 then
        Response.Write "An [HttpJdbc] error has occurred." & VBNewLine
        Response.write "   SQL State:" & connDB.Errors(0).sqlstate & VBNewLine
        Response.write " Description:" & connDB.Errors(0).Description  & VBNewLine
        Response.write "Native Error:" & connDB.Errors(0).NativeError & VBNewLine
        Response.write "         SQL:" & sSQL
    Else
		iFieldCount = rsResult.Fields.count - 1

		ReDim arrMetaDType(iFieldCount)
        For i = 0 to iFieldCount
            arrMetaDType(i) = 0
        Next

        'Loop through the recordset
        Do While Not rsResult.EOF
	        For i = 0 to iFieldCount
	        	iVarType = VarType(rsResult.Fields(i))
	            If iVarType = 0 Or VarType(rsResult.Fields(i)) = 1 then
		            Response.Write ("××××" & "ÿ")
				Else
		            If iVarType <> arrMetaDType(i) then
						arrMetaDType(i) = iVarType
		            End If
		            Response.Write (rsResult.Fields.Item(i) & "ÿ")
				End If
	        Next

            Response.Write ("þ")

            'Move to the next record in the recordset
            rsResult.MoveNext
        Loop

        Response.write(VBNewLine)
        Response.write("#@_METADATA_@#")
        For i = 0 to iFieldCount
	        Response.write(rsResult.Fields(i).Name & "=" & arrMetaDType(i) & "ÿ")
	    Next

        'Reset server objects
        rsResult.Close
        Set rsResult = Nothing
    End If
Else
    'Execute my update, insert, or delete
    connDB.Execute sSQL, lUpdated, adExecuteNoRecords

    If connDB.Errors.Count <> 0 then
        Response.Write "An [HttpJdbc] error has occurred." & VBNewLine
        Response.write "SQLstate=" & connDB.Errors(0).sqlstate & VBNewLine
        Response.write "Description=" & connDB.Errors(0).Description  & VBNewLine
        Response.write "NativeError=" & connDB.Errors(0).NativeError & VBNewLine
        Response.write "SQL=" & sSQL
    Else
        Response.write(lUpdated)
    End If
End if

On Error Goto 0
%>