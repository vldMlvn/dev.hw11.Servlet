import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String timeZoneId = req.getParameter("timezone");

        if (timeZoneId == null || timeZoneId.isEmpty()) {
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if (!cookie.getName().equals("lastTimezone")) {
                    timeZoneId = "+2";
                } else {
                    timeZoneId = cookie.getValue();
                }
            }
        } else {
            Cookie timezoneCookie = new Cookie("lastTimezone", timeZoneId);
            resp.addCookie(timezoneCookie);
        }

        String time = getCurrentTimeInTimeZone(timeZoneId);

        Context context = new Context();
        context.setVariable("timezoneId", timeZoneId);
        context.setVariable("currentTime", time);

        String html = engine.process("time", context);

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
}