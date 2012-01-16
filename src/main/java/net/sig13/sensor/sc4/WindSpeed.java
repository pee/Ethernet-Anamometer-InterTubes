//
//
//
//
package net.sig13.sensor.sc4;

import org.apache.log4j.*;
import org.json.*;

import javax.naming.*;
import java.sql.*;
import javax.sql.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * select * from ( select @row := @row +1 AS rownum, data, time from ( select @row := 0) r, 90A2DA0021AC_inspeedD2 ) ranked where rownum %10 = 1;
 */
public class WindSpeed extends HttpServlet {

    private static final Logger logger = Logger.getLogger(WindSpeed.class);
    private static final int MIN_REDUCE = 2;
    private static final int MAX_REDUCE = Integer.MAX_VALUE;
    // FIXME: Precompile for speed
    private static final String dateRegex = "(\\d\\d\\d\\d)\\D(\\d\\d)\\D(\\d\\d)";
    //private static final String reduceRegex = "(\\d)";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Context initCtx = null;
        Context envCtx;
        DataSource ds;
        Connection conn = null;
        PrintWriter out = null;

        boolean doDateSelect = false;
        boolean doSelectAll = false;
        boolean doReduce = false;
        int rFactor = 1;

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

            String startDate = request.getParameter("startDate");
            String stopDate = request.getParameter("stopDate");
            String reductionFactor = request.getParameter("reductionFactor");

            // Check/parse startDate and stopDate selectors if they exist
            if ((startDate != null) && (stopDate != null)) {

                Pattern datePattern = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE);
                Matcher dateMatcher = datePattern.matcher(startDate);

                if (dateMatcher.matches() == false) {
                    logger.fatal("Invalid startDate:" + startDate + ":");
                    response.sendError(500, "Invalid startDate:" + startDate + ":");
                    return;
                }

                dateMatcher = datePattern.matcher(stopDate);
                if ((stopDate.equalsIgnoreCase("now") == false) && dateMatcher.matches() == false) {
                    logger.fatal("Invalid stopDate:" + stopDate + ":");
                    response.sendError(500, "Invalid stopDate:" + stopDate + ":");
                    return;
                }

                doDateSelect = true;

            }

            if (reductionFactor != null) {
                logger.warn("doing reductionFactor");

//                Pattern reductionPattern = Pattern.compile(reduceRegex, Pattern.CASE_INSENSITIVE);
//                Matcher reductionMatcher = reductionPattern.matcher(reductionFactor);
//
//                if (reductionMatcher.matches() == false) {
//                    logger.fatal("Invalid reductionFactor:" + reductionFactor + ":");
//                    response.sendError(500, "Invalid reductionFactor:" + reductionFactor + ":");
//                    return;
//                }

                try {
                    rFactor = Integer.decode(reductionFactor);
                } catch (Exception e) {
                    logger.fatal("Invalid reductionFactor conversion:" + reductionFactor + ":");
                    response.sendError(500, "Invalid reductionFactor conversion:" + reductionFactor + ":");
                    return;
                }

                if ((rFactor < MIN_REDUCE) || rFactor > MAX_REDUCE) {
                    logger.fatal("Invalid reductionFactor value:" + reductionFactor + ":");
                    response.sendError(500, "Invalid reductionFactor value:" + reductionFactor + ":");
                    return;
                }

                doReduce = true;
            }

            Enumeration<String> pnames = request.getParameterNames();

            while (pnames.hasMoreElements()) {
                String pname = pnames.nextElement();
                logger.warn("Checking:" + pname + ":");
                if (pname.compareToIgnoreCase("all") == 0) {
                    doSelectAll = true;
                }
            }


            conn = ds.getConnection();

            Statement s = conn.createStatement();

            // s.execute("(select * from 90A2DA0021AC_inspeedD2 order by time desc limit 5000 ) order by time");

            boolean ex;
            if (doSelectAll == true) {

                if (doReduce == true) {

                    String query = "select time,data from ";
                    query += "( select @row := @row +1 AS rownum, data, time from ";
                    query += "( select @row := 0) r, 90A2DA0021AC_inspeedD2 ) ";
                    query += "ranked where rownum %" + rFactor + " = 1 order by time";

                    logger.warn(query);
                    ex = s.execute(query);

                } else {
                    logger.warn("selectAll no bounds");
                    ex = s.execute("select * from 90A2DA0021AC_inspeedD2 order by time");
                }

            } else if (doDateSelect == true) {

                if (doReduce == true) {

                    String query = "select time,data from ";
                    query += "( select @row := @row +1 AS rownum, data, time from ";
                    query += "( select @row := 0) r, 90A2DA0021AC_inspeedD2 ) ";
                    query += "ranked where ";
                    query += " ( time > '" + startDate + "') ";
                    query += " and ( time < '" + stopDate + "')";
                    query += " and rownum %" + rFactor + " = 1 order by time";

                    logger.warn("reduce with date");
                    logger.warn(query);
                    ex = s.execute(query);

                } else {

                    String query = "select * from 90A2DA0021AC_inspeedD2 ";
                    query += " where ( time > '" + startDate + "') ";
                    query += " and ( time < '" + stopDate + "')";
                    query += " order by time";

                    logger.warn(query);
                    ex = s.execute(query);
                }

            } else {
                ex = s.execute("(select * from 90A2DA0021AC_inspeedD2 order by time desc limit 5000 ) order by time");
            }

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
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
}
