/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sig13.sensor.sc4;

import org.apache.log4j.*;
import org.json.*;

import javax.naming.*;
import java.sql.*;
import javax.sql.*;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * @author pee
 */
public class PressureAll extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PressureAll.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Context initCtx = null;
        Context envCtx = null;
        DataSource ds = null;
        Connection conn = null;
        PrintWriter out = null;

        try {

            initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");
            ds = (DataSource) envCtx.lookup("jdbc/SC4");

            // i have a feeling this doesn't really work here ;)
            if (ds == null) {
                logger.fatal("DataSource came back null");
                response.sendError(500, "DataSource came backnull");
                return;
            }

            conn = ds.getConnection();

            Statement s = conn.createStatement();

            boolean ex = s.execute("(select * from 90A2DA0021AC_pressure order by time desc limit 40000 ) order by time");

            ResultSet rs = s.getResultSet();

            JSONArray jar = new JSONArray();


            while (rs.next()) {

                JSONArray point = new JSONArray();
                Timestamp ts = rs.getTimestamp(1);
                Float value = rs.getFloat(2);
                point.put(ts.getTime());
                point.put(value);

                jar.put(point);
            }

            String jsonCrap = jar.toString();
            response.setContentType("application/json;charset=UTF-8");
            out = response.getWriter();
            out.println(jsonCrap);


        } catch (Exception e) {
            logger.error(e, e);

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    initCtx.close();
                    //envCtx.close();
                } catch (Exception e2) {
                    logger.error(e2, e2);
                }

            }

            out.close();

        }


    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
