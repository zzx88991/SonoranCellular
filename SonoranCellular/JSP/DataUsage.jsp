<%@ page language="java" contentType="text/html" import="SonoranCellular.utils.OracleConnect,java.sql.*"  %>


<%  
    String accNumb = (String)session.getAttribute("acctNum");
        String acctName=(String)session.getAttribute("acctName");

    if(accNumb==""||acctName==""){
        response.sendRedirect("../index.html");
    }
    out.println("<h3 style='position:absolute;left:2%;top:200px;color:#eeff00'>Welcome, "+acctName+"!</h3>");

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
        String query = "WITH SQ AS (SELECT * FROM ITEM NATURAL JOIN ISASSOCIATEDWITH WHERE ACCOUNTNUMBER = "+accNum+" ORDER BY  ITEMNUMBER) SELECT TO_CHAR( ROUND(SQ.ENDDATE,'MONTH') - INTERVAL '1' MONTH, 'MON-YYYY') AS PERIODMONTH, ITEMNUMBER,NAME, MOBILENUMBER , SQ.PLANNAME, ACTUALDATAUSAGE, Plan.AllowedDataUsage FROM SQ JOIN SUBSCRIBE ON SQ.IMEI = SUBSCRIBE.IMEI JOIN PHONE ON SQ.IMEI = PHONE.IMEI JOIN ACCOUNT ON SUBSCRIBE.ACCOUNTNUMBER = ACCOUNT.ACCOUNTNUMBER JOIN PLAN on Plan.PlanName =SQ.PlanName ORDER BY SQ.ENDDATE,ITEMNUMBER";
        rs = s.executeQuery(query);




%>
<html>

    

    <head><title>SonoranCellular</title>
        <link type="text/css" rel="stylesheet" href="http://cgi.cs.arizona.edu/~cbustos/sonoran.css"/>

        <link rel='stylesheet' href='http://cgi.cs.arizona.edu/~zhouzx/css/progressbar.css'>

		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js"></script>
		<script type="text/javascript" src="http://cgi.cs.arizona.edu/~zhouzx/js/progressbar.js" >
		</script>
        <META http-equiv=Content-Type content="text/html">


    </head>
    <body link=#f0f0ff alink vlink=#f0f0ff>
        <h1 class="banner">
    <img src="http://cgi.cs.arizona.edu/~cbustos/images/banner.gif">
    </h1>
 
        <p>
            <center>
            
			<h1><img src="http://cgi.cs.arizona.edu/~zhouzx/images/datausage.png"></h1>            
            <br>
       

	    <table border="1" style="color:#eeff00">

        <%
        String currName="";
        String prevName="";

        int count = 0;
        while(rs.next()){
            out.println("<tr>");

            currName=rs.getString("PERIODMONTH");
            if(currName.compareTo(prevName)!=0){
                count++;
            	out.println("<td> <b> Bill End Period </b> </td><td> <b> Account Name </b> </td><td><b>Plan</b></td><td>  <b>Data Usage</b></td> </tr>");
                out.println("<td>"+currName+"</td>");
                prevName=currName;
            
            }else{
                out.println("<td>"+"</td>");

            }
            out.println( "<td>"+ rs.getString("Name")+"</td>");
            out.println( "<td>"+ rs.getString("PLANNAME")+"</td>");

            int usage = Integer.parseInt(rs.getString("ACTUALDATAUSAGE"));
            Double alloweddata=Integer.parseInt(rs.getString("AllowedDataUsage"))/100.00;
            double rate = usage/alloweddata;
            if(rate>=80){
            	out.println("<td><div class='meter red' style='width:200px'><span style=\'width:"+usage/alloweddata+"%\'></div></td>");
            }else if(rate >=55){
				out.println("<td><div class='meter orange' style='width:200px'><span style=\'width:"+usage/alloweddata+"%\'></div></td>");
        	}

            else{
				out.println("<td><div class='meter' style='width:200px'><span style=\'width:"+usage/alloweddata+"%\'></div></td>");
        	}
            out.println("<td>"+usage+"MB/"+Integer.parseInt(rs.getString("AllowedDataUsage"))+"MB</td>");
            out.println("<td>"+Math.round(usage/alloweddata*100)/100+"% Used</td>");
            out.println( "</tr>" );

        }
        if(count==0){
            out.println("<h1 style='color:#eeff00'>Sorry, we didn't not find any bills of your account.</h1>");
        }else{
            out.println("<h3 style='float:right'>Data usage of last <span style='color:red'>"+count+"</span> month(s)</h3>");
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



