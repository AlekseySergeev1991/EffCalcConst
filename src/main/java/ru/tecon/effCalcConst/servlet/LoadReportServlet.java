package ru.tecon.effCalcConst.servlet;

import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.report.ChangesReport;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@WebServlet("/loadReport")
public class LoadReportServlet extends HttpServlet {
    @EJB
    private EffCalcConstSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                URLEncoder.encode("История", "UTF-8") + " " +
                URLEncoder.encode("изменений", "UTF-8") + " " +
                URLEncoder.encode("параметра", "UTF-8") + " " +
                URLEncoder.encode(".xlsx", "UTF-8") + "\"");
        resp.setCharacterEncoding("UTF-8");

        try (OutputStream output = resp.getOutputStream()) {
            ChangesReport.createReport(id, bean).write(output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
