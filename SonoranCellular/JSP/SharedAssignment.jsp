<%@ page language="java" contentType="text/html" import="SonoranCellular.utils.OracleConnect,java.sql.*"  %>

<%  
    String accNumb = (String)session.getAttribute("acctNum");
    String acctName=(String)session.getAttribute("acctName");

    if(accNumb==""||acctName==""){
        response.sendRedirect("../index.html");
    }
    out.println("<h3 style='position:absolute;left:2%;top:200px;color:lime'>Welcome, "+acctName+"!</h3>");
    int accNum=0;
    try{
        accNum = Integer.parseInt(accNumb);
    }
    catch(Exception e){
        out.println("Error: user is not correctly logged in.");
        session.setAttribute("acctNum", null);
        session.setAttribute("acctName", null);
        session.invalidate();
        response.sendRedirect("../index.html");
    }
    //out.println(accNum);

    Connection m_conn;
    Statement s;
    try {

        Class.forName("oracle.jdbc.OracleDriver"); 
        m_conn = DriverManager.getConnection(OracleConnect.connect_string,OracleConnect.user_name,OracleConnect.password);  //get a connection
        if (m_conn == null) throw new Exception("getConnection failed");
        m_conn.setAutoCommit(true);//optional, but it sets auto commit to true
        s = m_conn.createStatement(); //create a statement
        if (s == null) throw new Exception("createStatement failed");
        ResultSet rs;
        String query = "With accNum AS (select distinct MasterAccountNumber from Owns where MasterAccountNumber="+accNum+" UNION select distinct DependantAccountNumber from Owns where MasterAccountNumber="+accNum+") Select distinct PlanName, Name FROM Subscribe left join Account on Account.AccountNumber=Subscribe.AccountNumber where Subscribe.AccountNumber IN (select * from accNum) OR Subscribe.AccountNumber="+accNum+" ORDER BY PlanName";
        rs = s.executeQuery(query);




%>




<html>

    

    <head><title>SonoranCellular</title>
        <link type="text/css" rel="stylesheet" href="http://cgi.cs.arizona.edu/~cbustos/sonoran.css"/>
        <META http-equiv=Content-Type content="text/html">


    </head>
    <body link=#f0f0ff alink vlink=#f0f0ff>
        <h1 class="banner">
    <img src="http://cgi.cs.arizona.edu/~cbustos/images/banner.gif">
    </h1>
 
        <p>
            <center>
            
            <h1><img src="http://cgi.cs.arizona.edu/~zhouzx/images/shared.png"></h1>            
            <br>
       

	    <table border="1" style="color:lime">
	      <tr> 
	      <td> <b> Plan Name </b> </td>
	      <td> <b> Account Name </b> </td>
        </tr>
        <%
        String currName="";
        String prevName="";
        int count=0;
        while(rs.next()){
            
            out.println("<tr>");

            currName=rs.getString("PlanName");
            if(currName.compareTo(prevName)!=0){
                count++;
                out.println("<td>"+currName+"</td>");
                prevName=currName;
            
            }else{
                out.println("<td>"+"</td>");

            }
            out.println( "<td>"+ rs.getString("Name")+"</td>");
            out.println( "</tr>" );

        }
        if(count==0){
            out.println("<h1 style='color:lime'>Sorry, we didn't not find any plans of your account.</h1>");        
        }

        }catch(Exception e){
            out.println(e);
        }

	      
        %>
	      </table>	

                <br><br>

                <table>
                <tr>
                <td>
                <form name="mainmenu" action=../LoginServlet method=get>
                <input type=submit name="MainMenu" value="Main Menu">
                </form>
                </td>
                </tr>
                <tr>
                <td>
                <form name="logout" action=../LoginServlet method=get >
                <input type=submit name="logout" value="Logout">
                </form>
                </td>
                </tr>
                </table>


            </center>

        </p>
    </body>
</html>


