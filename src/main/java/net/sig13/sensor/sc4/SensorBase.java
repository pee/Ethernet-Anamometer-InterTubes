//
//
//
package net.sig13.sensor.sc4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.json.JSONArray;

//
//
//
abstract public class SensorBase extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SensorBase.class);
    // FIXME: Precompile in static initiator for speed
    protected static final String dateRegex = "(\\d\\d\\d\\d)\\D(\\d\\d)\\D(\\d\\d)";
    protected static final String lastRegex = "(\\d+)";
    //
    private static final int MIN_REDUCE = 2;
    private static final int MAX_REDUCE = Integer.MAX_VALUE;
    //
    protected static final String PARAMATER_LAST = "last";
    protected static final String PARAMATER_START_DATE = "startDate";
    protected static final String PARAMATER_STOP_DATE = "stopDate";
    protected static final String PARAMATER_ALL = "all";
    protected static final String PARAMATER_REDUCTION = "reductionFactor";
    protected static final String PARAMATER_NEWEST_FIRST = "newestFirst";
    //
    //

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
    protected void processRequest(String sensorName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Context initCtx = null;
        Context envCtx;
        DataSource ds;
        Connection conn = null;
        //
        boolean querySet = false;
        //
        boolean doReduction = false;

        PreparedStatement ps = null;

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
            assert (conn != null);

            boolean doNewestFirst = false;

            String newestFirstString = request.getParameter(PARAMATER_NEWEST_FIRST);
            if ((newestFirstString == null) || newestFirstString.isEmpty()) {
                doNewestFirst = false;
            } else {
                doNewestFirst = true;
            }

            if (validReductionQuery(request, response)) {
                doReduction = true;
            }

            if (validLastQuery(request, response)) {
                ps = buildLastQuery(sensorName, request, conn, doReduction, doNewestFirst);
                querySet = true;
            }

            if (validDateQuery(request, response)) {
                ps = buildDateQuery(sensorName, request, conn, doReduction, doNewestFirst);
                querySet = true;
            }

            if (validAllQuery(request, response)) {
                ps = buildAllQuery(sensorName, request, conn, doReduction, doNewestFirst);
                querySet = true;
            }

            if (querySet == false) {
                ps = buildGenericQuery(sensorName, request, conn, doReduction, doNewestFirst);
            }

            boolean ex = ps.execute();

            if (ex == false) {
                response.sendError(500, "false return from execute");
                return;
            }

            ResultSet rs = ps.getResultSet();
            assert (rs != null);

            JSONArray jar = encodeToJSON(rs);

            sendJSONArray(jar, response);


        } catch (Exception e) {
            logger.error(e, e);

        } finally {

            if (conn != null) {
                try {

                    if (conn.isClosed() == false) {
                        conn.close();
                    }
                    initCtx.close();

                } catch (Exception e2) {
                    logger.error(e2, e2);
                }
            }

        }
    }

    /**
     *
     * @param request
     * @return
     */
    protected boolean validLastQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String last = request.getParameter(PARAMATER_LAST);

        if (last == null) {
            return false;
        }

        Pattern lastPattern = Pattern.compile(lastRegex, Pattern.CASE_INSENSITIVE);
        Matcher lastMatcher = lastPattern.matcher(last);

        if (lastMatcher.matches() == false) {
            String error = "Invalid last request:" + last + ":";
            logger.fatal(error);
            response.sendError(500, error);
            throw new IllegalArgumentException(error);
        }

        return true;

    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    protected boolean validReductionQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String reductionFactor = request.getParameter(PARAMATER_REDUCTION);

        int rFactor;

        if (reductionFactor == null || reductionFactor.isEmpty()) {
            //logger.warn("Reduction factor was null or empty");
            return false;
        }

        try {

            rFactor = getReductionFactor(request);

        } catch (Exception e) {
            String error = "Invalid reductionFactor conversion:" + reductionFactor + ":";
            logger.error(error);
            response.sendError(500, error);
            throw new IllegalArgumentException(error);
        }

        if ((rFactor < MIN_REDUCE) || rFactor > MAX_REDUCE) {
            String error = "Invalid reductionFactor value:" + reductionFactor + ":";
            logger.fatal(error);
            response.sendError(500, error);
            throw new IllegalArgumentException(error);
        }

        return true;

    }

    /**
     *
     * @param request
     * @return
     */
    protected int getReductionFactor(HttpServletRequest request) {

        int rFactor;

        String reductionFactor = request.getParameter(PARAMATER_REDUCTION);

        rFactor = Integer.decode(reductionFactor);

        return rFactor;
    }

    /**
     *
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    protected PreparedStatement buildLastQuery(String sensorName, HttpServletRequest request, Connection conn, boolean doReduction, boolean doNewestFirst) throws SQLException {

        PreparedStatement ps;

        StringBuilder select = new StringBuilder();
        String last = request.getParameter(PARAMATER_LAST);

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        if (doReduction == true) {

            int rFactor = this.getReductionFactor(request);

            select.append("select time,data from ");
            select.append("( select @row := @row +1 AS rownum, data, time from ");
            select.append("( select @row := 0) r, ").append(sensorName).append(" ) ");
            select.append("ranked where ");
            select.append(" ( time > DATE_SUB(CURDATE(),INTERVAL ? DAY ) ) ");
            select.append(" and ( time < 'now' )");
            select.append(" and rownum %? = 1 ");
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());
            ps.setInt(1, Integer.parseInt(last));
            ps.setInt(2, rFactor);

        } else {

            select.append("select * from ");
            select.append(sensorName);
            select.append(" where ( time > DATE_SUB(CURDATE(),INTERVAL ? DAY ) ) ");
            select.append(" and ( time < 'now')");
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());
            ps.setInt(1, Integer.parseInt(last));

        }

        logger.debug("buildLastQuery:" + select);

        return ps;

    }

    /**
     *
     * @param request
     * @return
     */
    protected boolean validDateQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String startDate = request.getParameter(PARAMATER_START_DATE);
        String stopDate = request.getParameter(PARAMATER_STOP_DATE);

        if (startDate == null || stopDate == null) {
            logger.debug("stardate/stopdate was null:" + startDate + ':' + stopDate + ":");
            return false;
        }

        Pattern datePattern = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(startDate);

        if (dateMatcher.matches() == false) {
            String error = "Invalid startDate:" + startDate + ":";
            logger.fatal(error);
            response.sendError(500, error);
            throw new IllegalArgumentException(error);
        }

        dateMatcher = datePattern.matcher(stopDate);
        if ((stopDate.equalsIgnoreCase("now") == false) && dateMatcher.matches() == false) {
            String error = "Invalid stopDate:" + stopDate + ":";
            logger.fatal(error);
            response.sendError(500, error);
            throw new IllegalArgumentException(error);
        }

        return true;

    }

    /**
     *
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    protected PreparedStatement buildDateQuery(String sensorName, HttpServletRequest request, Connection conn, boolean doReduction, boolean doNewestFirst) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        String stopDate = request.getParameter(PARAMATER_STOP_DATE);
        String startDate = request.getParameter(PARAMATER_START_DATE);

        StringBuilder select = new StringBuilder();

        if (doReduction == true) {

            int rFactor = getReductionFactor(request);

            select.append("select time,data from ");
            select.append("( select @row := @row +1 AS rownum, data, time from ");
            select.append("( select @row := 0) r, ").append(sensorName).append(" ) ");
            select.append("ranked where ");
            select.append(" ( time > ? ) ");
            select.append(" and ( time < ? )");
            select.append(" and rownum %? = 1 ");
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());
            ps.setString(1, startDate);
            ps.setString(2, stopDate);
            ps.setInt(3, rFactor);

        } else {

            select.append("select * from ");
            select.append(sensorName);
            select.append(" where ( time > ? ) ");
            select.append(" and ( time < ? )");
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());
            ps.setString(1, startDate);
            ps.setString(2, stopDate);

        }

        logger.debug("buildDateQuery:" + select);

        return ps;

    }

    /**
     *
     * @param request
     * @return
     */
    protected boolean validAllQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Enumeration<String> pnames = request.getParameterNames();

        while (pnames.hasMoreElements()) {
            String pname = pnames.nextElement();
            if (pname.compareToIgnoreCase(PARAMATER_ALL) == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    protected PreparedStatement buildAllQuery(String sensorName, HttpServletRequest request, Connection conn, boolean doReduction, boolean doNewestFirst) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        StringBuilder select = new StringBuilder();

        if (doReduction) {

            int rFactor = getReductionFactor(request);

            select.append("select time,data from ");
            select.append("( select @row := @row +1 AS rownum, data, time from ");
            select.append("( select @row := 0) r, ").append(sensorName).append(" ) ");
            select.append("ranked where rownum %? = 1 ");
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());
            ps.setInt(1, rFactor);

        } else {

            select.append("select * from ");
            select.append(sensorName);
            select.append(" order by time ");
            if (doNewestFirst) {
                select.append(" DESC ");
            }

            ps = conn.prepareStatement(select.toString());

        }

        logger.debug("buildAllQuery:" + select);


        return ps;

    }

    /**
     *
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    protected PreparedStatement buildGenericQuery(String sensorName, HttpServletRequest request, Connection conn, boolean doReduction, boolean doNewestFirst) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        // FIXME: this ignores reduction right now
        if (doReduction == true) {
            logger.warn("reduction set, don't care");
        }

        StringBuilder select = new StringBuilder();
        select.append("(select * from ");
        select.append(sensorName);
        select.append(" order by time desc limit 5000 ) ");
        select.append(" order by time ");
        if (doNewestFirst) {
            select.append(" DESC ");
        }


        logger.debug("buildGenericQuery:" + select);

        ps = conn.prepareStatement(select.toString());

        return ps;

    }

    /**
     *
     * @param jar
     * @param response
     * @throws IOException
     */
    protected void sendJSONArray(JSONArray jar, HttpServletResponse response) throws IOException {

        PrintWriter out;

        String jsonCrap = jar.toString();
        response.setContentType("application/json;charset=UTF-8");
        out = response.getWriter();
        out.println(jsonCrap);
        out.close();

    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    protected JSONArray encodeToJSON(ResultSet rs) throws SQLException {

        JSONArray jar = new JSONArray();

        while (rs.next()) {

            JSONArray point = new JSONArray();
            Timestamp ts = rs.getTimestamp(1);
            Float value = rs.getFloat(2);
            point.put(ts.getTime());
            point.put(value);

            jar.put(point);
        }

        return jar;
    }
}
