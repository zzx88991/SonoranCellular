/* ---------------------------------------------------------------
   * Title:	Sonoran Cellular Web Application, Assignment 8
   * Author:	Carla Bustos and Zixiang Zhou
   * Written:	December 3, 2014
   * Course:	Database Design, CSc 460
   * Prof:	Dr. Richard Snodgrass
   * File:  LoginServlet.java
   * Description:
   *		This program is a java servlet that accepts an account name
   *		and account number and verifies that they are in the 
   *		database, then starts their session.
   --------------------------------------------------------------- */

package SonoranCellular.servlets;
import java.util.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import SonoranCellular.servlets.*;
import SonoranCellular.utils.*;
import java.sql.*;
import java.util.regex.*;

public class LoginServlet extends HttpServlet
{
    public LoginServlet()
    {
        super();
    }

    OracleConnect oc = new OracleConnect();
    private boolean DEBUG = true;
    private String errorMsg = new String();


    public void drawHeader(HttpServletRequest req, PrintWriter out)
    {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>SonoranCellular logged in</title>");
        out.println("<link type=\"text/css\"");
        out.println(" rel=\"stylesheet\" ");
        out.println("href=\"http://cgi.cs.arizona.edu/~cbustos/sonoran.css\"/>");
        out.println("</head>");

        out.println("<body>");
		  out.println("<h1 class=\"banner\">");
        out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/banner.gif\">");
		  out.println("</h1>");

        out.println("<h2 class=\"banner\">");
        out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/login.gif\">");
        out.println("</h2>");

    }

    public void drawFooter(HttpServletRequest req, PrintWriter out)
    {
        out.println("</body>");
        out.println("</html>");
    }


    private void drawActiveOptions(HttpServletRequest req, PrintWriter out)
    {
		  out.println("<p class=\"welcome\" style='padding-left:270px'>");
		  out.println("<table>");
		  out.println("<tr><td class=\"center\">");
		  out.println("<form name=\"AddPlan\" action=AddPlan method=get>");
        out.println("<input type=submit name=\"AddPlan\" value=\"Add a Plan\">");
        out.println("</form>");
		  out.println("</td></tr>");
		  out.println("<tr><td class=\"center\">");
        out.println("<form name=\"FindBill\" action=FindBill method=get>");
        out.println("<input type=submit name=\"FindBill\" value=\"Print Bill for a billing period\">");
        out.println("</form>");
		  out.println("</td></tr>");
		  out.println("<tr><td class=\"center\">");
        out.println("<form name=\"PlanShare\" action=./JSP/SharedAssignment.jsp>");
        out.println("<input type=submit name=\"SharedAssignment\" value=\"Who is assigned to the same plan?\">");
        out.println("</form>");
		  out.println("</td></tr>");
        out.println("<tr><td class=\"center\">");
        out.println("<form name=\"DataUsage\" action=./JSP/DataUsage.jsp method=get>");
        out.println("<input type=submit name=\"Data Usage\" value=\"Check the data usage of recent months\">");
        out.println("</form>");
      out.println("</td></tr>");
		  out.println("<tr><td class=\"center\">");
        out.println("<form name=\"logout\" action=LoginServlet>");
        out.println("<input type=submit name=\"logout\" value=\"Log out\">");
        out.println("</form>");
		  out.println("</td></tr></table>");
		  out.println("</p>");
    }

    private void drawFailOptions(HttpServletRequest req, PrintWriter out)
    {
        // check for error messages
        if (!errorMsg.isEmpty())
        {
           out.println("<p class=\"error\">" + errorMsg + "</p>");
           errorMsg = new String();
        }
        else 
        {
           out.println("<p class=\"error\">");
			  out.println("Error: Enter the correct account number and name.");
			  out.println("</p>");
        }
		  out.println("<p class=\"welcome\">");
        out.println("<form name=\"logout\" action=index.html>");
        out.println("<input type=submit name=\"home\" value=\"Return to Main Menu\">");
        out.println("</form>");
	     out.println("</p>");
    }

    public void drawLoginSuccess(HttpServletRequest req, PrintWriter out,String aName)
    {
        drawHeader(req,out);
        out.println("<h3 style='position:absolute;left:2%;top:200px;color:#ff00ff'>Welcome, "+aName+"!</h3>");

        drawActiveOptions(req,out);
        drawFooter(req,out);
    }

    public void drawLoginFail(HttpServletRequest req, PrintWriter out)
    {
        drawHeader(req,out);
        drawFailOptions(req,out);
        drawFooter(req,out);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        System.out.println("LoginServlet: doGet(): started.");
        res.setContentType("text/html");
        HttpSession session = req.getSession();

        //check if user need to logout.
        if(req.getParameter("logout")!=null){
          session.setAttribute("acctNum",null);
          session.setAttribute("acctName",null);
          session.invalidate();
          res.sendRedirect("index.html");
        }

        PrintWriter out = res.getWriter();


        try
        {           

           String aNum = req.getParameter("acctNum");
           String aName = req.getParameter("acctName");

           if ((aNum == null) || (aNum.isEmpty()))
           {
              // then try to get it from the session variable
              if (session != null)
              {
                 aNum = (String) session.getAttribute("acctNum");
                 if ((aNum == null) || (aNum.isEmpty()))
                 {
                    errorMsg = "Error: Login info for account not available.  Try again.";
                    throw new ServletException("LoginServlet:doGet():Error: no account number data.");
                 }
              }
           }

           if ((aName == null) || (aName.isEmpty()))
           {
              if (session != null)
              {
                 aName = (String) session.getAttribute("acctName");
                 if ((aName == null) || (aName.isEmpty()))
                 {
                    errorMsg = "Error: Login info for name not available.  Try again.";
                    throw new ServletException("LoginServlet:doGet():Error: no account number data.");
                 }
              }
           }
          
           if(verifyUser(aNum, aName)){
               drawLoginSuccess(req,out,aName);
               // set the session attributes
               session.setAttribute("acctNum", aNum);
               session.setAttribute("acctName", aName);
           }else{
                drawLoginFail(req,out);

           }
          
        }
        catch (Exception ex)
        {
           System.out.println(ex.toString());
           drawLoginFail(req,out);
        }
    }

    private boolean verifyUser(String p_acctNum, String p_acctName) throws LoginFailException
    {
       int intAcctNum = -1;

         if ((p_acctNum == null) || (p_acctNum.isEmpty()))
         {
            errorMsg = "Error: Account number must not be null.";
            System.out.println("verifyUser(): account number is null or empty.");
            throw new LoginFailException("verifyUser(): Input error: account number null or empty.");
         }

         if (p_acctNum.length() > 10)
         {
            errorMsg = "Error: Account number is longer than 10 digits.";
            System.out.println("verifyUser(): account number is too long.");
            throw new LoginFailException("verifyUser(): Input error: account number too long.");
         }

         if ((p_acctName == null) || (p_acctName.isEmpty()))
         {
            errorMsg = "Error: Account name must not be null.";
            System.out.println("verifyUser(): account name is null or empty.");
            throw new LoginFailException("verifyUser(): Input error: account name null or empty.");
         }

         if (p_acctNum.length() > 10)
         {
            errorMsg = "Error: Account name is longer than 25 characters.";
            System.out.println("verifyUser(): account name is too long.");
            throw new LoginFailException("verifyUser(): Input error: account name too long.");
         }

          // check that acct number is all numeric input
          Pattern p = Pattern.compile("[^0-9]");
          Matcher m = p.matcher(p_acctNum);
          boolean b = m.find();

          if (b)
          {
             errorMsg = "Error: Account number must be numeric.";
             throw new LoginFailException("verifyUser(): Input error: Non-numeric" + 
                  "characters in acctNum");
          }

          p = Pattern.compile("[^a-zA-Z ]");
          m = p.matcher(p_acctName);
          b = m.find();

          if (b)
          {
             errorMsg = "Error: Account Name must be alpha characters and spaces.";
             throw new LoginFailException("verifyUser(): Input error: Non-alpha" +
                   " characters in acctName");
          }
      try
      {
          // convert the acctNum input to integer
          intAcctNum = Integer.parseInt(p_acctNum);

          // first, register Drivers
          Class.forName("oracle.jdbc.OracleDriver");
          Connection conn = DriverManager.getConnection(oc.connect_string, 
                            oc.user_name, oc.password);
          if (conn == null) throw new Exception("getConnection failed");
          try
          {
             conn.setAutoCommit(true);
             Statement s = conn.createStatement();
             if (s==null) throw new Exception("verifyUser():create s failed.");

             // do the query for the existance of the account number
             ResultSet result = s.executeQuery("SELECT COUNT(*) FROM ACCOUNT " +
                                  "WHERE AccountNumber = " + intAcctNum);
                                 

             // evaluate the result set, change flag if valid
             if (result.next())
             {
                int count = result.getInt("COUNT(*)");
                                System.out.println(count);

                if (count > 1)
                {
                   errorMsg = "Error: account number has duplicates.  Contact DBA.";
                   throw new LoginFailException("verifyUser(): too many records for this attempt.");
                }
                else if (count == 0)
                {
                   errorMsg = "Error: account number does not exist.";
                   throw new LoginFailException("verifyUser(): account does not exist.");
                }
             }
             else
             {
                errorMsg = "Error: Login could not be processed. Contact DBA";
                throw new LoginFailException("verifyUser(): result set of query 1 is empty");
             }

             //  query for matching account name
             result = s.executeQuery("SELECT COUNT(*) FROM ACCOUNT " +
                         "WHERE AccountNumber = " + intAcctNum + " AND " +
                         "Name = '" + p_acctName + "'");

             //  check result set for matching account name
             if (result.next())
             {
                int count = result.getInt("COUNT(*)");
                if (count > 1)
                {
                   errorMsg = "Error: account name has duplicates.  Contact DBA.";
                   throw new LoginFailException("verifyUser(): too many records with same name.");
                }
                else if (count == 0)
                {
                   errorMsg = "Error: Account name does not match.";
                   throw new LoginFailException("verifyUser(): acct name could not be recognized");
                }
             }
             else 
             {
                errorMsg = "Error: Login could not be processed.  Contact DBA.";
                throw new LoginFailException("verifyUser(): Result set empty.");
             }

             conn.commit();
             conn.close();
          }
          finally
          {
             if (conn != null) 
                conn.close();
          }
       }
       catch (Exception ex)
       {
          System.out.println("verifyUser(): Error: " + ex.toString());
          return false;
       }
       // return whether the user is valid
       return true;
    } // end of verifyUser()
   /* -----------------------------------------------------
      Private Class Name: LoginFailException
      Purpose:  help with handling bad input
      ----------------------------------------------------- */  
   private class LoginFailException extends Exception
   {
      public LoginFailException() {}
      public LoginFailException(String message)
      {
         super(message);
      }
   } // end of inner class LoginFailException
} // end of LoginServlet class


