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
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;

//
//
//
abstract public class SensorBase extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SensorBase.class);
    protected static final String dateRegex = "(\\d\\d\\d\\d)\\D(\\d\\d)\\D(\\d\\d)";
    protected static final String lastRegex = "(\\d+)";
    //
    protected static final String PARAMATER_LAST = "last";
    protected static final String PARAMATER_START_DATE = "startDate";
    protected static final String PARAMATER_STOP_DATE = "stopDate";
    protected static final String PARAMATER_ALL = "all";

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
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    protected PreparedStatement buildLastQuery(String sensorName, HttpServletRequest request, Connection conn) throws SQLException {

        PreparedStatement ps;

        StringBuilder select = new StringBuilder();
        String last = request.getParameter(PARAMATER_LAST);

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        select.append("select * from ");
        select.append(sensorName);
        select.append(" where ( time > DATE_SUB(CURDATE(),INTERVAL ? DAY ) ) ");
        select.append(" and ( time < 'now')");
        select.append(" order by time");

        logger.debug("buildLastQuery:" + select);

        ps = conn.prepareStatement(select.toString());
        ps.setInt(1, Integer.parseInt(last));

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
    protected PreparedStatement buildDateQuery(String sensorName, HttpServletRequest request, Connection conn) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        String stopDate = request.getParameter(PARAMATER_STOP_DATE);
        String startDate = request.getParameter(PARAMATER_START_DATE);

        StringBuilder select = new StringBuilder();

        select.append("select * from ");
        select.append(sensorName);
        select.append(" where ( time > ? ) ");
        select.append(" and ( time < ? )");
        select.append(" order by time");

        logger.debug("buildDateQuery:" + select);

        ps = conn.prepareStatement(select.toString());
        ps.setString(1, startDate);
        ps.setString(2, stopDate);

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
    protected PreparedStatement buildAllQuery(String sensorName, HttpServletRequest request, Connection conn) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        StringBuilder select = new StringBuilder();

        select.append("select * from ");
        select.append(sensorName);
        select.append(" order by time");

        logger.debug("buildAllQuery:" + select);

        ps = conn.prepareStatement(select.toString());

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
    protected PreparedStatement buildGenericQuery(String sensorName, HttpServletRequest request, Connection conn) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        StringBuilder select = new StringBuilder();
        select.append("(select * from ");
        select.append(sensorName);
        select.append(" order by time desc limit 5000 ) order by time");
        logger.debug("guildGenericQuery:" + select);

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
