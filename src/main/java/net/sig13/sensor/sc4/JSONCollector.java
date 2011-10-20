/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sig13.sensor.sc4;

//
import org.apache.log4j.*;

//
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * @author pee
 */
public class JSONCollector extends HttpServlet {

    private static final Logger logger = Logger.getLogger(JSONCollector.class);

    // limit our input to 10k of json content
    // This should probably be a property.
    private static final int MAX_JSON_LENGTH = 10000;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet JSONCollector</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet JSONCollector at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

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
     * Handles the HTTP <code>PUT</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int contentLength = request.getContentLength();
        logger.debug("ContentLength: " + contentLength);

        if (contentLength > MAX_JSON_LENGTH) {
            String em = "Content length of " + contentLength + " too long, max is " + MAX_JSON_LENGTH;
            logger.error(em);
            response.sendError(400, em);
            return;
        }

        String contentType = request.getContentType();
        logger.debug("ContentType: " + contentType);

        if ("application/json".equalsIgnoreCase(contentType)) {

            JSONHandler jh = new JSONHandler();
            jh.handle(request, response);
            return;

        }

        response.sendError(400, "Content type not supported");

        response.flushBuffer();


    }
    //
    //
    //
}
//
//