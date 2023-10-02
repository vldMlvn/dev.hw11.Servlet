import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebFilter(value = "/time/*")
public class TimezoneValidateFilter extends HttpFilter {
    private static final String INVALID_TIMEZONE_MESSAGE = "Invalid timezone UTC %s";
    private static final int MIN_GMT = -11;
    private static final int MAX_GMT = 12;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String GMT = req.getParameter("timezone");

        if (isValidGMT(GMT) || GMT == null) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(400);

            String responseText = String.format("<html><body>\n<center><h1>" + INVALID_TIMEZONE_MESSAGE
                    + "</h1></center>\n</body></html>", GMT);

            try (PrintWriter writer = res.getWriter()) {
                writer.write(responseText);
            }
        }
    }

    private boolean isValidGMT(String GMT) {
        try {
            int hours = Integer.parseInt(GMT);
            return hours >= MIN_GMT && hours <= MAX_GMT;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

