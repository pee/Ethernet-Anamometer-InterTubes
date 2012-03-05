//
//
//
package net.sig13.sensor.sc4;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

//
//
//
public class Pressure0 extends SensorBase {

    private static final Logger logger = Logger.getLogger(Pressure0.class);
    //
    private static final String SENSOR_NAME = "90A2DA0021AC_pressure";

    /**
     *
     * Override here because i prefer less data from the sensor for my graph,
     * FIXME: make less weird
     *
     * @param sensorName
     * @param request
     * @param conn
     * @return
     * @throws SQLException
     */
    @Override
    protected PreparedStatement buildGenericQuery(String sensorName, HttpServletRequest request, Connection conn, boolean doReduction, boolean doNewestFirst) throws SQLException {

        PreparedStatement ps;

        if (sensorName == null) {
            throw new NullPointerException("sensorName cannot be null");
        }

        StringBuilder select = new StringBuilder();
        select.append("(select * from ");
        select.append(sensorName);
        select.append(" order by time desc limit 2000 ) ");
        select.append(" order by time ");
        if ( doNewestFirst ) {
            select.append(" DESC ");
        }

        logger.debug("buildGenericQuery:" + select);

        ps = conn.prepareStatement(select.toString());

        return ps;

    }

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
        processRequest(SENSOR_NAME, request, response);
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
        processRequest(SENSOR_NAME, request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return SENSOR_NAME + " sensor data";
    }
}
