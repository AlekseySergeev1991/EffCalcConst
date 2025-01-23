package ru.tecon.effCalcConst.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.report.ChangesReport;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@WebServlet("/loadReport")
public class LoadReportServlet extends HttpServlet {
    @EJB
    private EffCalcConstSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        int obj_id = Integer.parseInt(req.getParameter("objId"));
        int repType = Integer.parseInt(req.getParameter("repType"));

        resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
        if (repType == 0) {
            String struct = bean.getStruct(obj_id);
            structCorrection(resp, struct);
        } else if (repType == 1) {
            String struct = bean.getObjName(obj_id);
            structCorrection(resp, struct);
        } else {
            String paramName = bean.getConstName(id);
            paramName = paramName.replace('/', '_');
            paramName = paramName.replaceAll(" ", "_");
            paramName = paramName.replaceFirst("\"", "_");
            paramName = paramName.replaceAll("\"", "");
            String struct;
            if (repType == 2) {
                struct = bean.getStruct(obj_id);
            } else {
                struct = bean.getObjName(obj_id);
            }
            struct = struct.replace('/', '_');
            struct = struct.replaceAll(" ", "");
            struct = struct.replaceFirst("\"", "_");
            struct = struct.replaceAll("\"", "");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode("Изменения", "UTF-8") + " " +
                    URLEncoder.encode("значения", "UTF-8") + " " +
                    URLEncoder.encode(paramName, "UTF-8") + " " +
                    URLEncoder.encode("для", "UTF-8") + " " +
                    URLEncoder.encode(struct, "UTF-8") + " " +
                    URLEncoder.encode(".xlsx", "UTF-8") + "\"");
            resp.setCharacterEncoding("UTF-8");
        }


        try (OutputStream output = resp.getOutputStream()) {
            ChangesReport.createReport(id, obj_id, bean).write(output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void structCorrection(HttpServletResponse resp, String struct) throws UnsupportedEncodingException {
        struct = struct.replace('/', '_');
        struct = struct.replaceAll(" ", "");
        struct = struct.replaceFirst("\"", "_");
        struct = struct.replaceAll("\"", "");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                URLEncoder.encode("Изменение", "UTF-8") + " " +
                URLEncoder.encode("нормативных", "UTF-8") + " " +
                URLEncoder.encode("значений", "UTF-8") + " " +
                URLEncoder.encode(struct, "UTF-8") + " " +
                URLEncoder.encode(".xlsx", "UTF-8") + "\"");
        resp.setCharacterEncoding("UTF-8");
    }
}
