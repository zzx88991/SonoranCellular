/* ---------------------------------------------------------------
   * Title:	Sonoran Cellular Web Application, Assignment 8
   * Author:	Carla Bustos and Zixiang Zhou
   * Written:	December 3, 2014
   * Course:	Database Design, CSc 460
   * Prof:	Dr. Richard Snodgrass
   * File:  FindBill.java
   * Description:
   *		This program is a java servlet that finds all the bills
   *		for the customer logged in and allows them to select one
   *		bill, then prints out the summary of that bill.
   --------------------------------------------------------------- */

package SonoranCellular.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import SonoranCellular.utils.*;
import SonoranCellular.servlets.*;
import java.sql.*;
import java.util.*;

public class FindBill extends HttpServlet
{

   public FindBill()
   {
      super();
   }

   OracleConnect oc = new OracleConnect();
   String acctNum = new String();
   String mAcctNum = new String();
   String uAcctNum = new String();
   String acctName = new String();
   String errMsg = new String();
   ArrayList<ItemDetail> itemList;
   String dateString = new String();
   String[] billHeaders = new String[6];

   public void drawHeader(HttpServletRequest req, PrintWriter out)
   {
      out.println("<html>");
      out.println("<head><title>Find Bill</title>");
      out.println("<link type=\"text/css\"");
      out.println(" rel=\"stylesheet\" ");
      out.println("href=\"http://cgi.cs.arizona.edu/~cbustos/sonoran.css\"/>");
      out.println("</head>");
      out.println("<body>");

		out.println("<h1 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/banner.gif\">");
		out.println("</h1>");

      out.println("<h2 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/findbill.gif\">");
      out.println("</h2>");

      if (!errMsg.isEmpty())
      {
         out.println("<p class=\"error\">" + errMsg + "</p>");
         errMsg = new String();
      }

   }

   public void drawFooter(HttpServletRequest req, PrintWriter out)
   {

      out.println("<div>");
		out.println("<table class=\"bill\"><tr><td>");
      out.println("<form name=\"MainMenu\" action=LoginServlet method=get>");
      out.println("<input type=submit name=\"MainMenu\" value=\"Main Menu\">");
      out.println("</form>");
      out.println("</td></tr>");

      out.println("<tr><td>");
      out.println("<form name=\"Logout\" action=LoginServlet>");
      out.println("<input type=submit name=\"logout\" value=\"Logout\">");
      out.println("</form>");
      out.println("</td><tr></table><div>");
      
      out.println("</body>");
      out.println("</html>");
   }


   public void drawGetBill(HttpServletRequest req, PrintWriter out)
   {

      System.out.println("CSC460: in drawGetBill___");

      try
      {
         // first, make sure we can get the bill dates, acctNum already checked
         ArrayList<String> billList = this.getBillDates(uAcctNum);

			out.println("<div>");
			if (billList.size() == 0)
			{
				out.println("<h1 style='color:#00FFFF'>Sorry, there are no bills to display for this account.</h1>");
			}
			else
			{
      		out.println("<form name=\"billSearch\" action=FindBill method=get>");
         	out.println("<p class=\"bill\">Choose a bill end date: </p>");
         	out.println("<select name=\"billperiod\" size=\"1\">");
         	for (String s : billList)
         	{
            	out.println("<option value=\""+ s +"\">"+ s +"</option>");
         	}
         	out.println("</select>");
      		out.println("<input type=submit name=\"findBill\" value=\"Find\" >");
      		out.println("</form>");
			}
      }
      catch (Exception ex)
      {
         out.println("<p class=\'error\'>Error, could not find bills.</p>");
      }
		out.println("</div>");
   }



   public void drawShowInfo(HttpServletRequest req, PrintWriter out)
   {
		out.println("<div>");
      // print out the bill header
      out.println("<table class=\"bill\">");
      out.println("<tr><th class=\"left\">Sonoran Cellular</th></tr>");
      out.println("<tr><th class=\"left\">Billing Invoice</th></tr>");
      out.println("<tr><td>Primary Account Number:</td>");
      out.println("<td>" + billHeaders[0] + "</td></tr>");
      out.println("<tr><td>Primary Account Name:</td>");
      out.println("<td>" + billHeaders[1] + "</td></tr>");
      out.println("<tr><td>Cycle Start Date:</td>");
      out.println("<td>" + billHeaders[2] + "</td></tr>");
      out.println("<tr><td>Cycle End Date:</td>");
      out.println("<td>" + billHeaders[3] + "</td></tr>");
      out.println("<tr><td>Due Date:</td>");
      out.println("<td>" + billHeaders[4] + "</td></tr>");
      out.println("<tr><td>Bill Total:</td>");
      out.println("<td>" + billHeaders[5] + "</td></tr>");
      out.println("</table>");

      // print out the bill line item detail in html table
      out.println("<table class=\"bill\" id=\"detail\">");
      out.println("<caption> Bill Detail</caption>");
      out.println("<tr>");
      out.println("<th>Item</ht>");
      out.println("<th>SubAcct</th>");
      out.println("<th>Name</th>");
      out.println("<th>Phone</th>");
      out.println("<th>Plan</th>");
      out.println("<th>Data Usage</th>");
      out.println("<th>Amount</th>");
      out.println("</tr>");

      try
      {
         for (ItemDetail id : itemList)
         {
            out.println("<tr>");
            out.println("<td>" + id.itemNum + "</td>");
            out.println("<td class=\"center\">" + id.subAcctNum + "</td>");
            out.println("<td>" + id.name + "</td>");
            out.println("<td>" + id.getPhoneString() + "</td>");
            out.println("<td>" + id.plan + "</td>");
            out.println("<td class=\"center\">" + id.actualUsage + "</td>");
            out.println("<td>" + String.format("%5.2f", id.amount) + "</td>");
            out.println("</tr>");
         }

      }
      finally
      {
         out.println("</table>");
			out.println("</div>");
      }

   }


   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();

      drawHeader(req,out);

      try
      {
         // then retrieve the account number
         HttpSession session = req.getSession();

			if (session == null)
         {
				errMsg = "Error: account information can not be determined.";
            throw new ServletException("FindBill:doGet(): session is null.");
         }

         if ((uAcctNum = (String) session.getAttribute("acctNum")) == null)
         {
				errMsg = "Error: account number could not be determined.";
				throw new ServletException("FindBill:doGet(): account number is null.");
         }

			if ((acctName = (String) session.getAttribute("acctName")) == null)
 			{
				errMsg = "Error: account name could not be determined.";
				throw new ServletException("FindBill:doGet(): account name is null");
			}else{
          out.println("<h3 style='position:absolute;left:2%;top:200px;color:#00FFFF'>Welcome, "+acctName+"!</h3>");

      }


         if(req.getParameter("findBill") == null)
         {           
            drawGetBill(req,out);
         }
         else
         {
            try
            {
               billHeaders = this.getBillHeader(uAcctNum, req.getParameter("billperiod"));
               itemList = this.getLineItemDetail(billHeaders[0], req.getParameter("billperiod"));
               drawShowInfo(req,out);
					session.setAttribute("acctNum", uAcctNum);
           		session.setAttribute("acctName", acctName);
            }
            catch (Exception ex)
            {
               System.out.println("FindBill():doGet():Error:" + ex.toString());
            }
         }
      } 
      finally
      {
         drawFooter(req,out);
      }
   }

   /* -----------------------------------------------------
      Name: getBillDates
      Purpose:  supply the select element with bill dates
      Parameter: String acctNum - the user's account number
      Returns: ArrayList<String>() with dates of bills
      Side Effects: none
      ----------------------------------------------------- */  
   private ArrayList<String> getBillDates(String p_acctNum) throws Exception
   {
      ArrayList<String> billList = new ArrayList<String>();

      if ((p_acctNum == null) || (p_acctNum.length() == 0))
      {
         throw new Exception("getBillDates(): error: account number is empty.");
      }

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
             if (s==null) throw new Exception("getBillDates():create s failed.");

             StringBuilder query = new StringBuilder();
             query.append("SELECT EndDate ");
             query.append("FROM BILL ");
             query.append("WHERE AccountNumber = ");
             query.append(p_acctNum);
             query.append(" OR AccountNumber in ");
             query.append("(SELECT MasterAccountNumber ");
             query.append("FROM Owns ");
             query.append("WHERE DependantAccountNumber = ");
             query.append(p_acctNum);
             query.append(")");

             // do the query for the existance of the account number
             ResultSet result = s.executeQuery(query.toString());

             // add the results to an ArrayList
             while (result.next())
             {
                java.util.Date date = result.getDate("EndDate");
                billList.add(date.toString());
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
         System.out.println("getBillDates(): Error: " + ex.toString());
      }
      return billList;
   } // end of getBillDates()

   /* -------------------------------------------------------------
      Name: getBillHeader
      Purpose: query data for bill header and return as string array
               also sets the master account variable mAcctNum
      Parameters: String p_acctNum the account to search for
                  String p_billDate the date of the bill to retrieve
      Returns: String[] a 5 String array 
      Side Effects: sets class variable mAcctNum
      ------------------------------------------------------------- */
   private String[] getBillHeader(String p_acctNum, String p_billDate) throws 
		BillFindFailException
   {
      // check the data first
      if ((p_acctNum == null) || (p_acctNum.length() == 0))
      {
         throw new BillFindFailException("getBillHeader(): error: account number cannot be empty.");
      }

      if ((p_billDate == null) || (p_billDate.length() == 0))
      {
         throw new BillFindFailException("getBillHeader(): error: account number cannot be empty.");
      }
      String[] temp = new String[6];

      try
      {
	 Class.forName("oracle.jdbc.OracleDriver");  // Registers drivers

	 Connection m_conn = DriverManager.getConnection(oc.connect_string, 
                            oc.user_name, oc.password);
         if (m_conn == null) throw new Exception("getConnection failed");
         try
         {
            m_conn.setAutoCommit(true);//optional, but it sets auto commit to true
	    Statement s = m_conn.createStatement(); //create a statement
	    if (s == null) throw new Exception("createStatement failed");

            StringBuilder query = new StringBuilder();
            query.append("SELECT ACCOUNTNUMBER, NAME, STARTDATE, ENDDATE, ");
            query.append("DUEDATE FROM BILL NATURAL JOIN ACCOUNT WHERE (");
            query.append("ACCOUNTNUMBER = ");
            query.append(p_acctNum);
            query.append(" OR ACCOUNTNUMBER IN (SELECT MASTERACCOUNTNUMBER FROM ");
            query.append("OWNS WHERE DEPENDANTACCOUNTNUMBER = ");
            query.append(p_acctNum);
            query.append(")) AND ENDDATE = date '");
            query.append(p_billDate);
            query.append("'");

            ResultSet rs = s.executeQuery(query.toString());

            while (rs.next())
            {
               temp[0] = Integer.toString(rs.getInt("ACCOUNTNUMBER"));
               mAcctNum = temp[0];
               temp[1] = rs.getString("NAME");
               temp[2] = rs.getDate("STARTDATE").toString();
               temp[3] = rs.getDate("ENDDATE").toString();
               temp[4] = rs.getDate("DUEDATE").toString();
            }

	    m_conn.commit();
	    m_conn.close();
         }
         finally
         {
	    if (m_conn != null) m_conn.close();
         }

      }
      catch (Exception ex)
      {
         System.out.println("getBillHeader(): Error: " + ex.toString());
      }
 
      return temp;
   } // end of getBillHeader()

   /* -------------------------------------------------------------
      Name: getLineItemDetail
      Purpose: retrieves item details from db, makes ItemDetail
               objects and sends them in an arraylist
      Parameters: String p_mAcctNum, the master account number 
                  String p_billDate, the date of the bill detail to retrieve
      Returns: ArrayList<ItemDetail>, a list of all line item info
      Side effects: none
      ------------------------------------------------------------- */
   private ArrayList<ItemDetail> getLineItemDetail(String p_mAcctNum, String p_billDate) throws
		BillFindFailException
   {
      ArrayList<ItemDetail> tempList = new ArrayList<ItemDetail>();
	   double billTotal = 0.0;

      // check that the account number isn't empty
		if ((p_mAcctNum == null) || (p_mAcctNum.isEmpty()))
		{
			errMsg = "Error: Account data could not be determined.";
			throw new BillFindFailException("getLineItemDetail(): account number is empty or null.");
		}

      // Check that the date string isn't empty
		if ((p_billDate == null) || (p_billDate.isEmpty()))
		{
			errMsg = "Error: Requested bill could not be determined.";
			throw new BillFindFailException("getLineItemDetail(): bill date is empty or null.");
		}		

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
             if (s==null) throw new Exception("getBillDates():create s failed.");

             // now build the query for all the info we need, it's long
             StringBuilder query = new StringBuilder();
             query.append("WITH SQ AS (SELECT * FROM ITEM NATURAL JOIN ");
             query.append("ISASSOCIATEDWITH WHERE ACCOUNTNUMBER = ");
             query.append(p_mAcctNum);
             query.append(" AND ENDDATE = date '");
             query.append(p_billDate); 
             query.append("' ORDER BY ITEMNUMBER) ");
             query.append("SELECT ITEMNUMBER, SUBSCRIBE.ACCOUNTNUMBER, ");
             query.append("NAME, MOBILENUMBER, SQ.PLANNAME, ACTUALDATAUSAGE, ");
             query.append("AMOUNT FROM SQ JOIN SUBSCRIBE ON SQ.IMEI = SUBSCRIBE.IMEI ");
             query.append("JOIN PHONE ON SQ.IMEI = PHONE.IMEI JOIN ACCOUNT ON ");
             query.append("SUBSCRIBE.ACCOUNTNUMBER = ACCOUNT.ACCOUNTNUMBER ");
             query.append("ORDER BY ITEMNUMBER");

             ResultSet rs = s.executeQuery(query.toString());             

             while (rs.next())
             {
                ItemDetail id = new ItemDetail();
                id.itemNum = rs.getInt("ITEMNUMBER");
                id.subAcctNum = rs.getInt("ACCOUNTNUMBER");
                id.name = rs.getString("NAME");
                id.mobileNumber = rs.getInt("MOBILENUMBER");
                id.plan = rs.getString("PLANNAME");
                id.actualUsage = rs.getDouble("ACTUALDATAUSAGE");
                id.amount = rs.getDouble("AMOUNT");
					 // while we are hear, update the total
					 billTotal += id.amount;
                tempList.add(id);
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
         System.out.println("getLineItemDetail(): Error: " + ex.toString());
      }

		// set the bill header index 5 - the total of the bill
		billHeaders[5] = String.format("&#36 %5.2f",billTotal);
      return tempList;
   }

   /* -------------------------------------------------------------
      Public class name: ItemDetail
      Purpose: to contain the values for the bill detail
      ------------------------------------------------------------- */
   public class ItemDetail
   {
      public int itemNum;
      public int subAcctNum;
      public String name;
      public int mobileNumber;
      public String plan;
      public double actualUsage;
      public double amount; 

      public ItemDetail()
      {
         itemNum = -1;
         subAcctNum = -1;
         name = new String();
         mobileNumber = -1;
         plan = new String();
         actualUsage = -1.0;
         amount = -1.0;
      };
 
      public String getPhoneString()
      {
         if (mobileNumber < 1)
         {
            return "";
         }
         else
         {
            String temp = Integer.toString(mobileNumber);
            return String.format("(%3s) %3s-%4s", temp.substring(0,3),
                  temp.substring(3,6), temp.substring(6));
         }
      }
      
   } // end of BillInfo class

   /* -----------------------------------------------------
      Private Class Name: BillFindFailException
      Purpose:  help with handling problems finding bills
      ----------------------------------------------------- */  
   private class BillFindFailException extends Exception
   {
      public BillFindFailException() {}
      public BillFindFailException(String message)
      {
         super(message);
      }
   } // end of inner class LoginFailException
}



