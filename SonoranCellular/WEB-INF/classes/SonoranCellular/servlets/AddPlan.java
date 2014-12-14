/* ---------------------------------------------------------------
   * Title:	Sonoran Cellular Web Application, Assignment 8
   * Author:	Carla Bustos and Zixiang Zhou
   * Written:	December 3, 2014
   * Course:	Database Design, CSc 460
   * Prof:	Dr. Richard Snodgrass
   * File:  AddPlan.java
   * Description:
   *		This program is a java servlet that allows an account owner
   *		to add a new phone with plan.  It updates the phone table
   *		and also the subscribe table.
   --------------------------------------------------------------- */

package SonoranCellular.servlets;
import java.util.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import SonoranCellular.utils.*;
import java.sql.*;
import SonoranCellular.servlets.*;
import java.util.regex.*;

public class AddPlan extends HttpServlet
{
   public AddPlan()
   {
      super();
   }

   OracleConnect oc = new OracleConnect();
   String plan_name = new String();
   String ph_num = new String();
   String imei = new String();
   String manuf = new String();
   String model = new String();
   String errMsg = new String();
   String acctNum = new String();
   String acctName = new String();

   public void drawUpdateMessage(HttpServletRequest req, PrintWriter out)
   {
      System.out.println("AddPlan:drawUpdateMessage(): started.");
		out.println("<p><table class=\"plan\">");
      out.println("<tr><td>Plan Name:</td>");
		out.println("<td>" + plan_name + "</td></tr>");
      out.println("<tr><td>IMEI:</td>");
		out.println("<td>" + imei + "</td></tr>");


		out.println("<tr><td>Mobile Number:</td><td>");
		out.println(String.format("(%3s) %3s-%4s",ph_num.substring(0,3), ph_num.substring(3,6),
			ph_num.substring(6)));
      out.println("</td></tr>");

      out.println("<tr><td>Manufacturer:</td>");
		out.println("<td>" + manuf + "</td></tr>");
      out.println("<tr><td>Model:</td>");
		out.println("<td>" + model + "</td></tr>");
		out.println("</table>");


		out.println("<table><tr><td>");
      out.println("<form name=\"MainMenu\" action=LoginServlet>");
      out.println("<input type=submit name=\"MainMenu\" value=\"MainMenu\">");
      out.println("</form>");
      out.println("</td></tr>");
		out.println("<tr><td>");
      out.println("<form name=\"logout\" action=index.html>");
      out.println("<input type=submit name=\"logoutSonoranCellular\" value=\"Logout\">");
      out.println("</form>");
		out.println("</td></tr></table></p>");
      System.out.println("AddPlan:drawUpdateMessage(): ended.");
   }


   public void drawHeader(HttpServletRequest req, PrintWriter out) {
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Plan Addition</title>");
      out.println("<link type=\"text/css\"");
      out.println(" rel=\"stylesheet\" ");
      out.println("href=\"http://cgi.cs.arizona.edu/~cbustos/sonoran.css\"/>");
      out.println("</head>");

      out.println("<body>");

		out.println("<h1 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/banner.gif\">");
		out.println("</h1>");

      out.println("<h2 class=\"banner\">");
      out.println("<img src=\"http://cgi.cs.arizona.edu/~cbustos/images/addplan.gif\">");
      out.println("</h2>");

   }


   public void drawFooter(HttpServletRequest req, PrintWriter out)
   {
      out.println("</body>");
      out.println("</html>");
   }


   public void drawAddPlanInformationMenu(HttpServletRequest req, PrintWriter out)
   {


      // check for error messages
      if (!errMsg.isEmpty())
      {
         out.println("<p class=\"error\">" + errMsg + "</p>");
         errMsg = new String();
			resetCurrent();
      }

      out.println("<p>");
      out.println("<form name=\"AddPlan\" action=AddPlan method=get>");
		out.println("<table class=\"plan\">");
		out.println("<tr>");
      out.println("<td>Plan Name:</td>");
      out.println("<td>");
      ArrayList<String> pList = this.getPlanNames();
      if (pList.size() == 0)
      {
         out.println("<input type=text name=\"planname\" size=\"25\">");
      }
      else
      {
         out.println("<select name=\"planname\" size=\"1\">");
         for (String s : pList)
         {
            out.println("<option value=\""+ s +"\">"+ s +"</option>");
         }
         out.println("</select>");
      }
	   out.println("</td>");
      out.println("</tr>");
      out.println("<tr><td>IMEI: </td>");
		out.println("<td>");
      out.println("<input type=text name=\"imei\" size=\"31\">");
      out.println("</td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td>Mobile Number: </td>");
		out.println("<td>");
      out.println("<input type=text name=\"mobilenumber\">");
      out.println("</td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td>Manufacturer: </td>");
		out.println("<td>");
      out.println("<input type=text name=\"manuf\" size=\"25\">");
      out.println("</td>");
      out.println("</tr>");      

      out.println("<tr>");
      out.println("<td>Model: </td>");
		out.println("<td>");
      out.println("<input type=text name=\"model\" size=\"10\">");
      out.println("</td>");
      out.println("</tr>"); 

      out.println("<tr>");
      out.println("<td>");
      out.println("<input type=submit name=\"Submit\" value=\"Add Plan\">&nbsp&nbsp");
      out.println("</td>");
      out.println("</tr>");
		out.println("</table>");
      out.println("</form>");

		out.println("<table>");


      out.println("<tr>");
      out.println("<td>");
      out.println("<form name=\"MainMenu\" action=LoginServlet>");
      out.println("<input type=submit name=\"MainMenu\" value=\"Return to Main Menu\">");
      out.println("</form>");
      out.println("</td>");
      out.println("</tr>");

      out.println("<tr>");
      out.println("<td>");
      out.println("<form name=\"logout\" action=LoginServlet>");
      out.println("<input type=submit name=\"logout\" value=\"Logout\">");
      out.println("</form>");
      out.println("</td>");
      out.println("</tr>");

      out.println("</table>");
		out.println("</p>");

   }


   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();

      drawHeader(req,out);

      // see if acctName and acctNum are available
      HttpSession session = req.getSession();

      acctNum = (String) session.getAttribute("acctNum");

      try
      {
         if (req.getParameter("Submit") == null)
         { 
            drawAddPlanInformationMenu(req,out);
         }
         else
         {
           
            try
            {
               checkIMEI(req.getParameter("imei"));
               checkMobile(req.getParameter("mobilenumber"));
               checkManufacturer(req.getParameter("manuf"));
               checkModel(req.getParameter("model"));
               checkPlanExistance(req.getParameter("planname"));
               if (processPhoneSubscribe())
               {
                  drawUpdateMessage(req,out);
               }
               else
               {
                  if (errMsg.isEmpty())
                     errMsg = "Error: request could not be processed.";
                  drawAddPlanInformationMenu(req,out);
               }
            }
            catch (Exception ex)
            {
					if (errMsg.isEmpty())
					{
						errMsg = "Error: your request to add a plan did not get processed.  Try again.";
					}
               System.out.println("doGet():" + ex.toString());
               drawAddPlanInformationMenu(req,out);
            }
         } // end of if (req.getParameter("Submit") == null)         
      }
      finally
      {
         acctName=(String)session.getAttribute("acctName");
         if(acctName!=null)
            out.println("<h3 style='position:absolute;left:2%;top:200px;color:#FF4500'>Welcome, "+acctName+"!</h3>");
         drawFooter(req,out);
      }
      
   } // end of doGet()

   /* -----------------------------------------------------
      Name: checkIMEI
      Purpose:  make sure the IMEI is compliant with DB IMEI
      Parameter: String p_IMEI - the user input for IMEI value
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves imei class variable
      ----------------------------------------------------- */
   private boolean checkIMEI(String p_IMEI) throws BadDataException
   {
		System.out.println("checkIMEI(): started.");
      // check that IMEI is more than 32 digits (because converting to int)
      if ((p_IMEI == null) || (p_IMEI.length() == 0))
      {
         errMsg = "Error: cannot leave IMEI blank";
         throw new BadDataException("checkIMEI(): Error: IMEI value is empty.");         
         //return false;
      }
      else if (p_IMEI.length() > 31)
      {
         errMsg = "Error: IMEI value you entered is too long.  Try again.";
         throw new BadDataException("checkIMEI(): Error: IMEI value is too long.");
         //return false;
      }

      // check that all characters are digits
      Pattern p = Pattern.compile("[^0-9]");
      Matcher m = p.matcher(p_IMEI);
      boolean b = m.find();

      if (b)
      {         
         errMsg = String.format("Error: IMEI must be only digits.  You entered %s", p_IMEI);
         throw new BadDataException("checkIMEI(): Error: IMEI value has non-digit characters.");
         //return false;
      }

      // call Integer try parse method
      imei = p_IMEI;

      return true;
   } // end of checkIMEI()

   /* -----------------------------------------------------
      Name: checkMobile
      Purpose:  make sure the input ph# is compliant with DB ph#
      Parameter: String p_phnum - the user input for mobile number value
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves phone number class variable
      ----------------------------------------------------- */
   private boolean checkMobile(String p_phNum) throws BadDataException
   {
		System.out.println("checkMobile(): started.");
      char[] digits = new char[10];

		if ((p_phNum == null) || (p_phNum.isEmpty()))
		{
			errMsg = "Error: Phone number cannot be left blank.  Please try again.";
			throw new BadDataException("checkMobile(): Error: Phone number left blank.");
		}

      int j = 0;
      for (int i = 0; (i < p_phNum.length()) && (j < 11); i++)
      {
         if (Character.isDigit(p_phNum.charAt(i)))
         {
				if (j==10)
				{
					errMsg = "Error: phone number has too many digits.  Please try again.";
					throw new BadDataException("checkMobile(): Error: Too many digits in phone num.");
				}
				else
				{
            	digits[j] = p_phNum.charAt(i);
            	j++;
				}
         } 
      }
      if (j < 10)
      {
         errMsg = "Error: Phone number does not have enough of digits.  Please try again.";
         throw new BadDataException("checkMobile(): Error: Phone too few digits.");
      }

      // save the mobile number because it's good
      try
      {
         ph_num = new String(digits);
      }
      catch (Exception ex)
      {
         System.out.println("checkMobile(): Error: " + ex.toString());
         return false;
      }
      return true;
   } // end of checkMobile

   /* -----------------------------------------------------
      Name: checkManufacturer
      Purpose:  make sure manufacturer is compliant with DB
      Parameter: String p_manuf - the user input for manufacturer
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves manufacturer class variable
      ----------------------------------------------------- */
   private boolean checkManufacturer(String p_manuf) throws BadDataException
   {
		System.out.println("checkManufacturer(): started.");
      if (p_manuf.length() == 0)
      {
         errMsg = "Error: Manufacturer name cannot be empty.  Try again.";
         throw new BadDataException("checkManufacturer(): Error: manuf name is empty.");
         //return false;
      }

      if (p_manuf.length() > 25)
      {
         errMsg = "Error: Manufacturer name is too long.  Try again.";
         throw new BadDataException("checkManufacturer(): Error: manuf name is too long.");
         //return false;
      }

      Pattern p = Pattern.compile("[^a-zA-Z ]");
      Matcher m = p.matcher(p_manuf);
      boolean b = m.find();

      if (b)
      {
         errMsg = "Manufacturer name contains non-alpha characters.  Try again.";
         throw new BadDataException("checkManufacturer(): Error: name contains non-alpha characters.");
         //return false;
      }
      manuf = p_manuf;
      return true;
   } // end of checkManufacturer()

   /* -----------------------------------------------------
      Name: checkModel
      Purpose:  make sure model is compliant with DB
      Parameter: String p_model - the user input for model
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves model class variable
      ----------------------------------------------------- */
   private boolean checkModel(String p_model) throws BadDataException
   {
		System.out.println("checkModel(): started.");
      if (p_model.length() == 0)
      {
         errMsg = "Error: Model name cannot be empty.  Try again.";
         throw new BadDataException("checkModel(): Error: model name is empty.");
      }

      if (p_model.length() > 10)
      {
         errMsg = "Error: Model name is too long.  Try again.";
         throw new BadDataException("checkModel(): Error: model name is too long.");
      }

      // check for special characters
      Pattern p = Pattern.compile("[^0-9a-zA-Z-_ ]");
      Matcher m = p.matcher(p_model);
      boolean b = m.find();

      if (b)
      {
         errMsg = "Error: model name must not have special characters. Please try again.";
         throw new BadDataException("checkModel(): Error: model name has special characters.");
      }
      model = p_model;
      return true;
   }  // end of checkModel()

   /* -----------------------------------------------------
      Name: checkPlanExistance
      Purpose:  make sure plan exists in DB
      Parameter: String p_planName - the user input for plan
      Returns: true - accept input, false - reject input
      Side Effects: on success, saves plan name class variable
      ----------------------------------------------------- */
   private boolean checkPlanExistance(String p_planName) throws BadDataException
   {
		System.out.println("checkPlanExistance(): started.");
      if (p_planName.isEmpty())
      {
         errMsg = "Error: plan name could not be determined.  Please try again";
         throw new BadDataException("checkPlanExistance(): error: plan name is empty");
      }
      if (p_planName.length() > 25)
      {
         errMsg = "Error: plan name is too long.  Please try again";
         throw new BadDataException("checkPlanExistance(): error: plan name is too long");
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
             if (s==null) throw new Exception("checkPlanExistance():create s failed.");

             // do the query for the existance of the account number
             ResultSet result = s.executeQuery("SELECT COUNT(*) FROM PLAN " +
                      "WHERE PlanName = '" + p_planName + "'");

             if (result.next())
             {
                int count = result.getInt("COUNT(*)");
                if (count == 0)
                {
                   errMsg = "Error: The plan you selected does not exist.  Please try again.";
                   throw new BadDataException("checkPlanExistance(): Error: plan does not exist.");
                }
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
         System.out.println("checkPlanExistance(): Error: " + ex.toString());
      }
      plan_name = p_planName;
      return true;
   }

   /* -----------------------------------------------------
      Name: processPhoneSubscribe
      Purpose:  see if phone exists in DB, insert if ok
      Parameter: int p_phNum - the user input for phone
      Returns: true - accept input, false - reject input
      Side Effects: on success, update to database
      ----------------------------------------------------- */
   private boolean processPhoneSubscribe() throws BadDataException
   {
		System.out.println("processPhoneSubscribe(): started.");
      if ( (acctNum.isEmpty()) || (manuf.isEmpty()) || (model.isEmpty()) || (plan_name.isEmpty()) )
      {
         System.out.println("processPhoneSubscribe():" + acctNum + "," + manuf + "," 
             + model + "," + plan_name);
         errMsg = "Error: not enough information to add phone to database.  Please try again.";
         throw new BadDataException("processPhoneSubscribe(): error: one of the strings is empty.");
      }

      if ( (ph_num == null) || (ph_num.isEmpty()) )
      {
         System.out.println("processPhoneSubscribe(): phone number is empty or null");
         errMsg = "Error: phone identifier information not set.  Please try again.";
         throw new BadDataException("processPhoneSubscribe(): error: phone number is empty or null.");
      }

		// do same for imei
      if ( (imei == null) || (imei.isEmpty()) )
      {
         System.out.println("processPhoneSubscribe(): imei is empty or null.");
         errMsg = "Error: IMEI information not set.  Please try again.";
         throw new BadDataException("processPhoneSubscribe(): error: IMEI is null or empty.");
      }

      try
      {
         // first, register Drivers
          Class.forName("oracle.jdbc.OracleDriver");
          Connection conn = DriverManager.getConnection(oc.connect_string, 
                            oc.user_name, oc.password);
         if (conn == null) throw new Exception("processPhoneSubscribe(): error: getConnection failed");
         try
         {
             conn.setAutoCommit(true);
             Statement s = conn.createStatement();
             if (s==null) throw new Exception("processPhoneSubscribe(): error: create s failed.");

             // do the query for the existance of the phone
             ResultSet result = s.executeQuery("SELECT IMEI, MobileNumber FROM PHONE " +
                      "WHERE IMEI = " + imei + " OR " +
                      "MobileNumber = " + ph_num);

             int count = 0;

             while (result.next())
             {
                count++;
             }

             if (count == 0)
             {
                // then insert the phone into the database
                s.execute("INSERT INTO PHONE VALUES("+imei+","+ph_num+",'"+manuf+"','"+model+"')");
                // now process the subscribe insert
                s.execute("INSERT INTO SUBSCRIBE VALUES ("+imei+","+acctNum+",'"+plan_name+"')");
             }
             else
             {
                errMsg = "Error: phone or IMEI already exists.";
                throw new BadDataException("processPhoneSubscribe(): error:ph_imei already exists.");
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
         System.out.println("processPhoneSubscribe(): Error: " + ex.toString());
         return false;
      }
      
      return true;
   } // end of processPhoneSubscribe()

	/* -----------------------------------------------------
      Name: resetCurrent()
		Purpose:	to reset the current fields after errors
		Parameters: none
		Returns: none
		SideEffects: the values for the insert record are reset
		----------------------------------------------------- */
	private void resetCurrent()
	{
		System.out.println("AddPlan:resetCurrent: info: reset current called.");
   	ph_num = new String();
   	imei = new String();
   	manuf = new String();
   	model = new String();   	
	}
       
   /* -----------------------------------------------------
      Name: getPlanNames
      Purpose:  supply the select element with plan name
      Parameter: none
      Returns: ArrayList<Integers>() with names of plans
      Side Effects: none
      ----------------------------------------------------- */  
   private ArrayList<String> getPlanNames()
   {
		System.out.println("getPlanNames(): started.");
      ArrayList<String> planList = new ArrayList<String>();

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
             if (s==null) throw new Exception("getPlanName():create s failed.");

             // do the query for the existance of the account number
             ResultSet result = s.executeQuery("SELECT PlanName FROM PLAN");

             // add the results to an ArrayList
             while (result.next())
             {
                planList.add(result.getString("PlanName"));
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
         System.out.println("getPlanName(): Error: " + ex.toString());
      }
      return planList;
   } // end of getPlanName

   /* -----------------------------------------------------
      Private Class Name: BadDataException
      Purpose:  help with handling bad input
      ----------------------------------------------------- */  
   public class BadDataException extends Exception
   {
      public BadDataException() {}
      public BadDataException(String message)
      {
         super(message);
      }
   } // end of inner class BadDataException
}
