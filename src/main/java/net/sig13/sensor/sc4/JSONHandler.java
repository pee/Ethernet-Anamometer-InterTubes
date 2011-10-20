/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sig13.sensor.sc4;

import net.sig13.sensor.sc4.method.*;

import org.apache.log4j.*;
import org.json.*;

//
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * @author pee
 */
public class JSONHandler {

    private final static Logger logger = Logger.getLogger(JSONHandler.class);

    //
    public JSONHandler() {
        logger.debug("JSONHandler()");
    }

    //
    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject job;
        String method;
        String sensorID;

        String data = buildData(request, response);

        logger.debug("Data: " + data);

        try {

            job = new JSONObject(data);

            if (job.has("method") == false) {
                String em = "method not found in json";
                logger.error(em);
                response.sendError(700, em);
                return;
            }

            method = job.getString("method");
            logger.debug("Found method: " + method);

            if (job.has("sensorID") == false) {
                response.sendError(701, "sensor ID not found in json");
                return;
            }
            sensorID = job.getString("sensorID");
            logger.debug("Found sensorID: " + sensorID);

            methodSwitcher(response, method, sensorID, job);

        } catch (JSONException ex) {
            logger.error(ex, ex);
            response.sendError(500, "Failed to parse json data:" + ex);
            return;
        }

        //response.sendError(200, "OK (not really!)");
    }

    //
    private void methodSwitcher(HttpServletResponse response, String method, String sID, JSONObject job) throws ServletException, IOException {

        if (method == null) {
            response.sendError(500, "Method cannot be null");
            return;
        }

        if (sID == null) {
            response.sendError(500, "sID cannot be null");
            return;
        }

        logger.debug("Method: " + method);
        logger.debug("SensorId: " + sID);

        if ("ping".equalsIgnoreCase(method)) {

            Ping ping = new Ping();
            ping.handle(response, sID, job);

            return;
        }

        if ("report".equalsIgnoreCase(method)) {

            Report report = new Report();
            report.handle(response, sID, job);

            return;
        }

        logger.warn("Unknown method returned:" + method + ":");
        response.sendError(405, "Unknown method:" + method + ":");


    }

    private String buildData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // read the json blob into a String for parsing
        BufferedReader reader = request.getReader();

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();

        while (line != null) {
            sb.append(line); //.append("\n");
            line = reader.readLine();
        }

        reader.close();
        String data = sb.toString();

        return data;
    }
    //
}
