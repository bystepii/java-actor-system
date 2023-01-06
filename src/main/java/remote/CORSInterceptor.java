package remote;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is a filter that intercepts all requests and adds the appropriate CORS headers.
 */
@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class CORSInterceptor implements Filter {

    /**
     * Allowed origins.
     */
    private static final String[] allowedOrigins = {
            "http://localhost:3000", "http://localhost:5500", "http://localhost:5501", "http://localhost:8080", "http://localhost:8888",
            "http://127.0.0.1:3000", "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080", "http://127.0.0.1:8888",
    };

    /**
     * Default constructor.
     */
    public CORSInterceptor() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String requestOrigin = request.getHeader("Origin");

        if (requestOrigin != null && isAllowedOrigin(requestOrigin)) {
            // Authorize the origin, all headers, and all methods
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", requestOrigin);
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", "*");
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods",
                    "GET, OPTIONS, HEAD, PUT, POST, DELETE");

            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            // CORS handshake (pre-flight request)
            if (request.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }
        // pass the request along the filter chain
        filterChain.doFilter(request, servletResponse);
    }

    /**
     * Checks if the given origin is allowed.
     *
     * @param origin the origin to check.
     * @return true if the origin is allowed, false otherwise.
     */
    private boolean isAllowedOrigin(String origin) {
        for (String allowedOrigin : allowedOrigins)
            if (origin.equals(allowedOrigin)) return true;
        return false;
    }
}