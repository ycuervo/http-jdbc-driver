<%
'Dimension variables
Dim sDSN			'Holds the DSN
Dim connDB 			'Holds the Database Connection Object
Dim arrMetaDType()  'Holds the resulset metadata information
Dim bResendMetaData 'Holds the flag to resend metadata information if needed
Dim rsResult		'Holds the recordset for the records in the database
Dim sSQL			'Holds the SQL query for the database
Dim sSession		'Holds the SessionID 
Dim sConnSession	'Holds the SessionID that this connection is working with 
Dim lAffected       'Holds the record affected by an insert, update or delete

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
        ReDim arrMetaDType(rsResult.Fields.count-1)
        
        For i = 0 to (rsResult.Fields.count-1)
            If i = 0 then
                Response.write("#@_METADATA_@#")
            End If
            arrMetaDType(i) = VarType(rsResult.Fields(i))
            Response.write(rsResult.Fields(i).Name & "=" & arrMetaDType(i) & "ÿ")
        Next

        Response.write(VBNewLine)

        'Loop through the recordset
        Do While Not rsResult.EOF
            bResendMetaData = false
            For i = 0 to (rsResult.Fields.count-1)
                If VarType(rsResult.Fields(i)) <> arrMetaDType(i) then
                    bResendMetaData = true
                    Exit For
                End If
            Next

            If bResendMetaData then
                Response.write(VBNewLine)
                For i = 0 to (rsResult.Fields.count-1)
                    If i = 0 then
                        Response.write("#@_METADATA_@#")
                    End If
                    arrMetaDType(i) = VarType(rsResult.Fields(i))
                    Response.write(rsResult.Fields(i).Name & "=" & arrMetaDType(i) & "ÿ")
                Next
                Response.write(VBNewLine)
            End If

            For i = 0 to (rsResult.Fields.count-1)
                Response.Write (rsResult.Fields.Item(i) & "ÿ")
            Next

            Response.Write ("þ")

            'Move to the next record in the recordset
            rsResult.MoveNext
        Loop

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