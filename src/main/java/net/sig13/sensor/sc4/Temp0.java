//
//
//
package net.sig13.sensor.sc4;

import java.io.IOException;
import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 *
 * @author pee
 */
public class Temp0 extends SensorBase {

    private static final Logger logger = Logger.getLogger(Temp0.class);
    //private static final String dateRegex = "(\\d\\d\\d\\d)\\D(\\d\\d)\\D(\\d\\d)";
    //private static final String lastRegex = "(\\d+)";
    //
    private static final String SENSOR_NAME = "10_F73ECB010800_temperature";

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
        boolean querySet = false;


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


            if (validLastQuery(request, response)) {
                ps = buildLastQuery(SENSOR_NAME, request, conn);
                querySet = true;
            }

            if (validDateQuery(request, response)) {
                ps = buildDateQuery(SENSOR_NAME, request, conn);
                querySet = true;
            }

            if (validAllQuery(request, response)) {
                ps = buildAllQuery(SENSOR_NAME, request, conn);
                querySet = true;
            }

            if (querySet == false) {
                ps = buildGenericQuery(SENSOR_NAME, request, conn);
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
