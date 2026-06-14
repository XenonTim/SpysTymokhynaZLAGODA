package org.example.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public final class HtmlPage {
    private HtmlPage() {}

    public static String esc(Object value) {
        if (value == null) return "";
        String s = String.valueOf(value);
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static void render(HttpServletResponse response, String title, String body, String backUrl) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"uk\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            out.println("<title>" + esc(title) + "</title>");
            out.println("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">");
            out.println("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css\" rel=\"stylesheet\">");
            out.println("<style>body{background:#f8f9fa}.card{border-radius:1rem}.table td,.table th{vertical-align:middle}</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container py-4\">");
            out.println("<div class=\"d-flex justify-content-between align-items-center mb-4\">");
            out.println("<h1 class=\"h3 mb-0\">" + esc(title) + "</h1>");
            if (backUrl != null) {
                out.println("<a class=\"btn btn-outline-secondary\" href=\"" + esc(backUrl) + "\"><i class=\"bi bi-arrow-left me-1\"></i>Назад</a>");
            }
            out.println("</div>");
            out.println(body);
            out.println("</div>");
            out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\"></script>");
            out.println("</body></html>");
        }
    }
}
