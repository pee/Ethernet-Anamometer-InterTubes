/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sig13.sensor.sc4.method;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;
import org.json.JSONObject;

/**
 *
 * @author pee
 */
public class Ping extends Method {

    private static final Logger logger = Logger.getLogger(Ping.class);

        public void handle(HttpServletResponse response, String sID, JSONObject job) throws ServletException, IOException {
            logger.info("Ping from sensor ID: " + sID + ":");
            response.setStatus(200);
        }

}
