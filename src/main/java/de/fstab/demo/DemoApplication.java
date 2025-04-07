package de.fstab.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * @return system environment variables, formatted as an HTML table with timestamp
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getEnvironment() {
        StringBuilder result = new StringBuilder();
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        // Create HTML response with Bootstrap styling
        result.append("<!DOCTYPE html>\n");
        result.append("<html lang=\"en\">\n");
        result.append("<head>\n");
        result.append("    <meta charset=\"UTF-8\">\n");
        result.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        result.append("    <title>Environment Variables</title>\n");
        result.append("    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n");
        result.append("    <style>\n");
        result.append("        body { padding: 20px; }\n");
        result.append("        .timestamp { margin-bottom: 20px; font-style: italic; }\n");
        result.append("    </style>\n");
        result.append("</head>\n");
        result.append("<body>\n");
        result.append("    <div class=\"container\">\n");
        result.append("        <h1 class=\"mt-4 mb-4\">Environment Variables</h1>\n");
        result.append("        <div class=\"timestamp\">Current time: ").append(timestamp).append("</div>\n");
        result.append("        <div class=\"table-responsive\">\n");
        result.append("            <table class=\"table table-striped table-hover table-bordered\">\n");
        result.append("                <thead class=\"table-dark\">\n");
        result.append("                    <tr>\n");
        result.append("                        <th>Variable</th>\n");
        result.append("                        <th>Value</th>\n");
        result.append("                    </tr>\n");
        result.append("                </thead>\n");
        result.append("                <tbody>\n");

        // Add environment variables to the table
        for (var e : new TreeMap<>(System.getenv()).entrySet()) {
            result.append("                    <tr>\n");
            result.append("                        <td>").append(stripAndTruncate(50, e.getKey())).append("</td>\n");
            result.append("                        <td>").append(stripAndTruncate(100, e.getValue())).append("</td>\n");
            result.append("                    </tr>\n");
        }

        result.append("                </tbody>\n");
        result.append("            </table>\n");
        result.append("        </div>\n");
        result.append("    </div>\n");
        
        // Add auto-refresh script
        result.append("    <script>\n");
        result.append("        setTimeout(function() { location.reload(); }, 30000); // Refresh every 30 seconds\n");
        result.append("    </script>\n");
        result.append("</body>\n");
        result.append("</html>\n");

        return result.toString();
    }

    private String stripAndTruncate(int length, String s) {
        return truncate(length, stripNewlinesAndTabs(s));
    }

    private String stripNewlinesAndTabs(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("\\s+", " ");
    }

    private String truncate(int length, String s) {
        if (s != null && s.length() > length) {
            return s.substring(0, length - 3) + "...";
        }
        return s;
    }

    /**
     * @return plain text version of environment variables for CLI clients
     */
    @GetMapping(value = "/plain", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getEnvironmentPlain() {
        StringBuilder result = new StringBuilder();
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        
        result.append("ENVIRONMENT (").append(timestamp).append(")\n");
        result.append("---------------------------------\n");
        
        for (var e : new TreeMap<>(System.getenv()).entrySet()) {
            // key length 30 so that KUBERNETES_SERVICE_PORT_HTTPS fits in
            // value length 42 so that the overall table fits in an 80 char terminal window
            result.append(String.format("| %-30s | %-42s |\n", stripAndTruncate(30, e.getKey()), stripAndTruncate(42, e.getValue())));
        }
        
        return result.toString();
    }
}
