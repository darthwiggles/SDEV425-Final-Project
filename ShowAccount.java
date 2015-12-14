/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDEV425_HW4;

import java.io.IOException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// DB resources
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import org.apache.derby.jdbc.ClientDataSource;

/**
 *
 * @author jim
 */
public class ShowAccount extends HttpServlet {

    // Variable
    private int user_id;
    private HttpSession session;
    // Database field data
    private String Cardholdername;
    private String CardType;
    private String CardNumber;
    private Date expiredate;
    
    static Cipher cipher;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        session = request.getSession(true);
        if (session.getAttribute("UMUCUserEmail") == null) {
            // Send back to login page 
            response.sendRedirect("login.jsp");
        } else {
            // Connect to the Database and pull the data
            getData();
            
            //Decrypt the data
            try {
                decryptData();
            }
            catch (Exception e) {
                System.out.println("An error has occurred.");
            }
            //Don't display the full data to the user
            sanitizeData();
            
            // Set the Attribute for viewing in the JSP
            request.setAttribute("Cardholdername", Cardholdername);
            request.setAttribute("CardType", CardType);
            request.setAttribute("CardNumber", CardNumber);
            request.setAttribute("expiredate", expiredate);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("account.jsp");
            dispatcher.forward(request, response);       
            
  
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void getData() {

        try {
            ClientDataSource ds = new ClientDataSource();
            ds.setDatabaseName("contact");
            ds.setServerName("localhost");
            ds.setPortNumber(1527);
            ds.setUser("nbuser");
            ds.setPassword("nbuser");
            ds.setDataSourceName("jdbc:derby");

            Connection conn = ds.getConnection();

            Statement stmt = conn.createStatement();
            String sql = "select user_id,Cardholdername,Cardtype,CardNumber,expiredate"
                    + " from customeraccount where user_id = " + session.getAttribute("UMUCUserID");
            ResultSet rs = stmt.executeQuery(sql);
            // Assign values
            while (rs.next()) {
                user_id = rs.getInt(1);
                Cardholdername = rs.getString(2);
                CardType = rs.getString(3);
                CardNumber = rs.getString(4);
                expiredate = rs.getDate(5);
            }

        } catch (Exception e) {
            System.out.println("An error has occurred.");
        }

    }
    
    public void decryptData() throws Exception {
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
		
        String keyString = "abcdefghijklmnopqrstuv";
        //Decode the base64 string
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        //Rebuild the key
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        cipher = Cipher.getInstance("AES");
        
        Base64.Decoder decoder = Base64.getDecoder();
	byte[] encryptedTextByte = decoder.decode(CardNumber);
	cipher.init(Cipher.DECRYPT_MODE, secretKey);
	byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
	String decryptedText = new String(decryptedByte);
        CardNumber = decryptedText;
    }
    
    /* Replace all but the last 4 of the card number with asterisks. */
    public void sanitizeData() {
        CardNumber = CardNumber.substring(CardNumber.length() - 4);
        CardNumber = ("****-****-****-" + CardNumber);
    }
}
