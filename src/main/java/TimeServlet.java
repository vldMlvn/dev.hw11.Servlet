import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String timeZoneId = req.getParameter("timezone");

        if (timeZoneId == null || timeZoneId.isEmpty()) {
            timeZoneId = "+2";
        }

        String time = getCurrentTimeInTimeZone(timeZoneId);

        String html = generateHtmlResponse(timeZoneId, time);

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(html);
        }
    }

    private String getCurrentTimeInTimeZone(String timeZoneId) {
        TimeZone timeZone = TimeZone.getTimeZone("GMT" + timeZoneId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(timeZone);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String generateHtmlResponse(String timeZoneId, String time) {
        return "<html><body>"
                + "\n<center>"
                + "\n<h1>Поточний час в зоні UTC " + timeZoneId + "</h1>"
                + "\n<p>" + time + "</p>"
                + "\n</center>"
                + "\n</body></html>";
    }
}
