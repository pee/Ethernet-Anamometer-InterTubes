//
//
//
//
package net.sig13.sensor.sc4;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * select * from ( select @row := @row +1 AS rownum, data, time from ( select @row := 0) r, 90A2DA0021AC_inspeedD2 ) ranked where rownum %10 = 1;
 */
public class WindSpeed extends SensorBase {

    private static final Logger logger = Logger.getLogger(WindSpeed.class);
    //
    private static final String SENSOR_NAME = "90A2DA0021AC_inspeedD2";

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(SENSOR_NAME, request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return SENSOR_NAME + " data";
    }
}
