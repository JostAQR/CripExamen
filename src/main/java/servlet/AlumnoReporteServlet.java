/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Quichiz
 */
@WebServlet(name = "AlumnoReporteServlet", urlPatterns = {"/AlumnoReporte"})
public class AlumnoReporteServlet extends HttpServlet {

    @PersistenceUnit(unitName = "com.mycompany_Preg01_war_1.0-SNAPSHOTPU")
    EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            EntityManager em = emf.createEntityManager();

            // Ruta del archivo .jasper
            String reportPath = getServletContext().getRealPath("/reportes/reportUsua.jasper");

            // Llenado del reporte con los datos JPA
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(reportPath));
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(em.createQuery("SELECT a FROM Alumnoweb a").getResultList());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

            response.setContentType("application/pdf");
            OutputStream outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
