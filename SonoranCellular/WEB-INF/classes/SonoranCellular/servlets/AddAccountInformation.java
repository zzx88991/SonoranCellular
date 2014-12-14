/* ---------------------------------------------------------------
   * Title:	Sonoran Cellular Web Application, Assignment 8
   * Author:	Carla Bustos and Zixiang Zhou
   * Written:	December 3, 2014
   * Course:	Database Design, CSc 460
   * Prof:	Dr. Richard Snodgrass
   * File:  AddAccountInformation.java
   * Description:
   *		This program is a java servlet that allows a person to
   *		enter an account number and name, if neither already
   *		exist in the database, then adds them and starts a session.
   --------------------------------------------------------------- */

package SonoranCellular.servlets;
import java.util.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import SonoranCellular.utils.*;
import SonoranCellular.servlets.*;
import java.util.regex.*;


public class AddAccountInformation extends HttpServlet
{
   public AddAccountInformation()
   {
      super();
   }

   OracleConnect oc = new OracleConnect();
   String aName = new String();
   int aNum = -1;
   String errMsg = new String();

   public void drawUpdateMessage(HttpServletRequest req, PrintWriter out)
   {
		out.println("<div>");
		out.println("<table class=\"addnew\">");
      out.println("<tr><td>Account Number:</td>");
		out.println("<td>" + aNum + "</td></tr>");      
      out.println("<tr><td>Account Name:</td>");
		out.println("<td>"+ aName + "</td></tr>");
		out.println("</table>");
      out.println("<form name=\"MainMenu\" action=LoginServlet method=get>");
      out.println("<input type=submit name=\"MainMenu\" value=\"Main Menu\">");
      out.println("</form>");

		out.println("</div>");
   }


   public void drawHeader(HttpServletRequest req, PrintWriter out) {
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Account Addition</title>");
      out.println("<link type=\"text/css\"");
      out.println(" rel=\"stylesheet\" ");
      out.println("href=\"http://cgi.cs.arizona.edu/~cbustos/sonoran.css\"/>");
      out.println("</head>");

      out.println("<body>");
      out.println("<h1 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/banner.gif\">");
	   out.println("</h1>");

      out.println("<h2 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/addaccount.gif\">");
      out.println("</h2>");
   }


   public void drawFooter(HttpServletRequest req, PrintWriter out)
   {
      out.println("</body>");
      out.println("</html>");
   }


   public void drawAddAccountInformationMenu(HttpServletRequest req, PrintWriter out)
   {
      // check for error messages
      if ((errMsg != null) && (!errMsg.isEmpty()))
      {
         out.println("<p class=\"error\">" + errMsg + "</p>");
         errMsg = new String();
      }

      out.println("<div>");
      out.println("<form name=\"AddAccountInformation\" action=AddAccountInformation method=get>");
	   out.println("<table class=\"addnew\">");
      out.println("<tr><td>");
      out.println("Account Number:");
      out.println("</td>");
      out.println("<td>");
      out.println("<input type=text name=\"accountnum\">");
      out.println("</td></tr>");

      out.println("<tr><td>");
      out.println("Account Name:");
      out.println("</td>");
      out.println("<td>");
      out.println("<input type=text name=\"accountname\">");
      out.println("</td>");
      out.println("</tr>");
      out.println("<tr>");
      out.println("<td>");
      out.println("<input type=submit name=\"Submit\" value=\"Submit\">&nbsp&nbsp");
      out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</form>");

      out.println("<table>");
      out.println("<tr>");
      out.println("<td>");
      out.println("<form name=\"Cancel\" action=index.html method=get>");
      out.println("<input type=submit name=\"Cancel\" value=\"Cancel\">&nbsp&nbsp");
      out.println("</form>");
      out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</div>");

   }


   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();

      drawHeader(req,out);
   
      try
      {
         HttpSession session = req.getSession();

         if(req.getParameter("Submit") == null)
         {
            drawAddAccountInformationMenu(req,out);
         }
         else
	 		{
            try
            {
               checkAccountInput(req.getParameter("accountnum"), req.getParameter("accountname"));
               if (insertAccount())
               {
	          		drawUpdateMessage(req,out);
						    session.setAttribute("acctNum", Integer.toString(aNum));
           			session.setAttribute("acctName", aName);
               }
               else
               {
                  if (errMsg.isEmpty())
                     errMsg = "Error: request could not be processed.";
                  drawAddAccountInformationMenu(req,out);
               }
            }
            catch (Exception ex)
            {
               System.out.println("doGet():" + ex.toString());
               drawAddAccountInformationMenu(req,out);
            }
	 }
      }
      finally
      {
         drawFooter(req,out);
      }
   }

   /* -----------------------------------------------------
      Name: checkAccountInput
      Purpose:  make sure the account is compliant with DB 
      Parameter: String p_acctNum - the user input for number
                 String p_acctName - user input for name
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves accountname
                    and accountnum class variable
      ----------------------------------------------------- */
   private boolean checkAccountInput(String p_acctNum, String p_acctName) throws AccountAddFailException
   {
      if (p_acctNum.isEmpty())
      {
         errMsg = "Error: Account Number cannot be empty.";
         throw new AccountAddFailException("checkAccountInput():Account number is an empty string.");
      }
      if (p_acctNum.length() > 10)
      {
         errMsg = "Error: Account Number is too long.";
         throw new AccountAddFailException("checkAccountInput():Account number is > 10.");
      }
      if (p_acctName.isEmpty())
      {
         errMsg = "Error: Account Name cannot be empty.";
         throw new AccountAddFailException("checkAccountInput():Account name is an empty string.");
      }
      if (p_acctName.length() > 25)
      {
         errMsg = "Error: Account Name is too empty.";
         throw new AccountAddFailException("checkAccountInput():Account name is > 25.");
      }
      // check that acct number is all numeric input
      Pattern p = Pattern.compile("[^0-9]");
      Matcher m = p.matcher(p_acctNum);
      boolean b = m.find();

      if (b)
      {
         errMsg = "Error: Account number must be numeric.";
         throw new AccountAddFailException("checkAccountInput(): Input error: Non-numeric" + 
              "characters in acctNum");
      }

      p = Pattern.compile("[^a-zA-Z ]");
      m = p.matcher(p_acctName);
      b = m.find();

      if (b)
      {
         errMsg = "Error: Account Name must be alpha characters and spaces.";
         throw new AccountAddFailException("verifyUser(): Input error: Non-alpha" +
                   " characters in acctName");
      }

      // now try to parse int
      aNum = Integer.parseInt(p_acctNum);
      aName = p_acctName;

      return true;
   } // end of checkAccountInput()

   /* -----------------------------------------------------
      Name: insertAccount
      Purpose: process insert statement to DB, after check 
      Parameter: none
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves accountname
                    and accountnum class variable
      ----------------------------------------------------- */
   private boolean insertAccount() throws AccountAddFailException
   {
      // first check the class variables
      if (aName.isEmpty())
      {
         errMsg = "Error: Account Name cannot be empty.";
         throw new AccountAddFailException("insertAccount():Account name is an empty string.");
      }
      if (aNum <= 0)
      {
         errMsg = "Error: Account Number invalid.";
         throw new AccountAddFailException("insertAccount():Account number is: " + aNum);
      }
      // begin handling database checking for non-duplicate
      try
      {
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
                                  "WHERE AccountNumber = " + aNum);

           if (result.next())
           {
              int count = result.getInt("COUNT(*)");
              if (count != 0)
              {
                 errMsg = "Error: Account Number already exists.";
                 throw new AccountAddFailException("insertAccount():Account number already exists.");
              }
           }
           else
           {
              errMsg = "Error: Account Number error.";
              throw new AccountAddFailException("insertAccount():Account number error.");
           }

           // now check the account name
           result = s.executeQuery("SELECT COUNT(*) FROM ACCOUNT WHERE Name = '" + aName + "'");
           if (result.next())
           {
              int count = result.getInt("COUNT(*)");
              if (count != 0)
              {
                 errMsg = "Error: Account Name already exists.";
                 throw new AccountAddFailException("insertAccount():Account name already exists.");
              }
           }
           else
           {
              errMsg = "Error: Account Name error.";
              throw new AccountAddFailException("insertAccount():Account name error.");
           }

           // now ready to insert into DB
           s.execute("INSERT INTO ACCOUNT VALUES("+ aNum + ", '" + aName + "')");
         }
         finally
         {
             if (conn != null) 
                conn.close();
         }
      }
      catch (Exception ex) 
      {
         System.out.println("insertAccount(): Error: " + ex.toString());
         return false;
      }
      return true;
   } // end of insertAccount()

   /* -----------------------------------------------------
      Private Class Name: AccountAddFailException
      Purpose:  help with handling bad input
      ----------------------------------------------------- */  
   private class AccountAddFailException extends Exception
   {
      public AccountAddFailException() {}
      public AccountAddFailException(String message)
      {
         super(message);
      }
   } // end of inner class LoginFailException
}
