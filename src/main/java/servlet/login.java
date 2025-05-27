package servlet;

import dao.AlumnowebJpaController;
import dto.Alumnoweb;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String passTest = "1234"; // Cambia esto por la contraseña que quieres guardar
            String hash = BCrypt.hashpw(passTest, BCrypt.gensalt());
            System.out.println("Contraseña en texto plano: " + passTest);
            System.out.println("Hash para BD: " + hash);
            String user = request.getParameter("user"); // DNI
            String pass = request.getParameter("pass"); // Password en texto plano

            if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
                out.println("{\"resultado\":\"error\", \"mensaje\":\"Faltan credenciales\"}");
                return;
            }

            AlumnowebJpaController aluDAO = new AlumnowebJpaController(emf);
            Alumnoweb usuario = aluDAO.findByDni(user);

            if (usuario != null && BCrypt.checkpw(pass, usuario.getPassEstd())) {
                HttpSession sesion = request.getSession(true);
                sesion.setAttribute("usuario", usuario);
                out.println("{\"resultado\":\"ok\"}");
            } else {
                out.println("{\"resultado\":\"error\", \"mensaje\":\"Usuario o contraseña incorrectos\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("{\"resultado\":\"error\", \"mensaje\":\"Error interno\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet de login con bcrypt";
    }
}
