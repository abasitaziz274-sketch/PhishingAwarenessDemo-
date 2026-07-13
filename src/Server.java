import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new LoginPageHandler());
        server.createContext("/submit", new SubmitHandler());
        server.createContext("/awareness", new AwarenessHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }

    static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static void logAccess(HttpExchange exchange, String route) {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) logDir.mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
            if (userAgent == null) userAgent = "unknown";

            FileWriter fw = new FileWriter("logs/access_log.txt", true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(timestamp + " | route: " + route + " | user-agent: " + userAgent);
            pw.close();
        } catch (IOException e) {
            System.out.println("Logging failed: " + e.getMessage());
        }
    }

    static class LoginPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logAccess(exchange, "/");
            File file = new File("public/login.html");
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logAccess(exchange, "/submit");

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<title>Session Analysis</title>"
                + "<style>"
                + "*{box-sizing:border-box;}"
                + "body{background:#0a0e0a;color:#39ff14;font-family:'Courier New',monospace;"
                + "display:flex;justify-content:center;align-items:flex-start;min-height:100vh;margin:0;padding:40px 20px;}"
                + ".box{max-width:640px;width:100%;background:#0d1410;border:1px solid #1f3d1f;border-radius:6px;"
                + "padding:32px 36px;box-shadow:0 0 25px rgba(57,255,20,0.15);}"
                + "h2{color:#ff5f56;font-size:17px;margin:0 0 16px 0;letter-spacing:1px;}"
                + "h3{color:#39ff14;font-size:13px;margin:26px 0 10px 0;letter-spacing:1px;border-top:1px solid #1f3d1f;padding-top:20px;}"
                + "p{font-size:13px;line-height:1.7;color:#8fdc8f;margin:0 0 12px 0;}"
                + "pre{background:#050a05;border:1px solid #245524;padding:12px;border-radius:4px;"
                + "font-size:12px;color:#39ff14;overflow-x:auto;margin:10px 0;}"
                + "ol{margin:0;padding-left:20px;color:#8fdc8f;font-size:12.5px;line-height:2;}"
                + "ol li strong{color:#39ff14;}"
                + ".stats{display:flex;gap:14px;margin:14px 0 4px 0;}"
                + ".stat{flex:1;background:#050a05;border:1px solid #245524;border-radius:4px;padding:12px;text-align:center;}"
                + ".stat b{display:block;font-size:20px;color:#39ff14;}"
                + ".stat span{font-size:10px;color:#5fbf5f;letter-spacing:0.5px;}"
                + "a{color:#39ff14;font-size:12px;text-decoration:none;}"
                + "a:hover{text-decoration:underline;}"
                + ".links{margin-top:20px;display:flex;gap:20px;}"
                + ".countdown{color:#e0b23c;font-size:11px;margin-top:18px;border-top:1px solid #1f3d1f;padding-top:14px;}"
                + "</style></head><body>"
                + "<div class='box'>"
                + "<h2>[!] SIMULATED PHISHING DEMO - NO DATA CAPTURED</h2>"
                + "<p>In a real attack, whatever you typed would have been silently "
                + "captured and logged by the attacker's server - you would never see this warning. "
                + "Here is exactly what would have happened behind the scenes:</p>"
                + "<h3>&gt; ANATOMY OF THIS ATTACK</h3>"
                + "<ol>"
                + "<li><strong>Convincing UI</strong> - a fake page styled to look like a trusted portal</li>"
                + "<li><strong>Form submission</strong> - browser sends your input via HTTP POST</li>"
                + "<li><strong>Data exfiltration</strong> - a real attacker's server logs it instantly</li>"
                + "<li><strong>Silent redirect</strong> - victim is bounced to a real site, unaware</li>"
                + "</ol>"
                + "<h3>&gt; EXAMPLE ATTACKER LOG ENTRY</h3>"
                + "<pre>2026-07-11 14:32:07 | username: john_doe | password: ********</pre>"
                + "<div class='stats'>"
                + "<div class='stat'><b>83%</b><span>ORGS PHISHED<br>PER YEAR</span></div>"
                + "<div class='stat'><b>1 in 4</b><span>CAN'T SPOT<br>A FAKE PAGE</span></div>"
                + "<div class='stat'><b>#1</b><span>BREACH ENTRY<br>METHOD</span></div>"
                + "</div>"
                + "<div class='links'>"
                + "<a href='/awareness'>&gt; View the awareness guide</a>"
                + "<a href='/'>&gt; Go back</a>"
                + "</div>"
                + "<div class='countdown'>Auto-returning to login in <span id='c'>59</span>s...</div>"
                + "</div>"
                + "<script>"
                + "let s=59;const el=document.getElementById('c');"
                + "setInterval(()=>{s--;el.textContent=s;if(s<=0)window.location='/';},1000);"
                + "</script>"
                + "</body></html>";

            sendHtml(exchange, html);
        }
    }

    static class AwarenessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logAccess(exchange, "/awareness");

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<title>Awareness Guide</title>"
                + "<style>"
                + "*{box-sizing:border-box;}"
                + "body{background:#0a0e0a;color:#39ff14;font-family:'Courier New',monospace;"
                + "padding:50px 20px;margin:0;display:flex;justify-content:center;}"
                + ".wrap{max-width:720px;width:100%;}"
                + "h1{color:#39ff14;font-size:20px;margin:0 0 6px 0;letter-spacing:1px;}"
                + ".sub{color:#5fbf5f;font-size:12px;margin-bottom:30px;}"
                + "h2{color:#e0b23c;font-size:13px;margin:30px 0 14px 0;letter-spacing:1px;"
                + "border-top:1px solid #1f3d1f;padding-top:22px;}"
                + ".card{background:#0d1410;border:1px solid #1f3d1f;border-radius:6px;padding:16px 20px;margin-bottom:12px;}"
                + ".card b{color:#39ff14;font-size:13px;display:block;margin-bottom:4px;}"
                + ".card p{color:#8fdc8f;font-size:12px;line-height:1.6;margin:0;}"
                + "table{width:100%;border-collapse:collapse;font-size:12px;margin-top:6px;}"
                + "th{color:#e0b23c;text-align:left;padding:8px;border-bottom:1px solid #245524;font-size:11px;}"
                + "td{padding:8px;border-bottom:1px solid #1a2e1a;color:#8fdc8f;}"
                + "td.bad{color:#ff8080;} td.good{color:#39ff14;}"
                + "ol{color:#8fdc8f;font-size:12.5px;line-height:2;padding-left:20px;}"
                + "ol li strong{color:#39ff14;}"
                + "a.back{color:#e0b23c;display:inline-block;margin-top:34px;font-size:12px;text-decoration:none;"
                + "border-top:1px solid #1f3d1f;padding-top:20px;width:100%;}"
                + "a.back:hover{text-decoration:underline;}"
                + "</style></head><body>"
                + "<div class='wrap'>"
                + "<h1>&gt; PHISHING AWARENESS GUIDE</h1>"
                + "<div class='sub'>How to recognize, avoid, and respond to fake login pages</div>"
                + "<h2>&gt; 01. WARNING SIGNS TO CHECK</h2>"
                + "<div class='card'><b>Wrong or unusual URL</b><p>Look closely at the address bar - phishing domains often use lookalike spellings, e.g. 'aiou-login.com' instead of the real domain.</p></div>"
                + "<div class='card'><b>No HTTPS / padlock</b><p>A missing padlock icon or 'Not Secure' warning means the connection is not encrypted - a major red flag on any login page.</p></div>"
                + "<div class='card'><b>Unsolicited urgency</b><p>Messages pushing you to 'log in immediately' or 'verify now' are a pressure tactic used to bypass careful thinking.</p></div>"
                + "<div class='card'><b>Off-brand details</b><p>Fake pages reuse a real logo but often get small details wrong - spacing, colour, or wording that feels slightly off.</p></div>"
                + "<div class='card'><b>Unexpected login prompts</b><p>Being asked to log in again from a link in an email or message you did not request is suspicious.</p></div>"
                + "<h2>&gt; 02. REAL vs FAKE - QUICK COMPARISON</h2>"
                + "<table><tr><th>Signal</th><th>Real Site</th><th>Fake Site</th></tr>"
                + "<tr><td>URL</td><td class='good'>Matches known domain exactly</td><td class='bad'>Slightly altered / misspelled</td></tr>"
                + "<tr><td>HTTPS</td><td class='good'>Padlock always present</td><td class='bad'>Often missing or fake</td></tr>"
                + "<tr><td>Arrival</td><td class='good'>You navigated there yourself</td><td class='bad'>Clicked from an unexpected link</td></tr>"
                + "<tr><td>Tone</td><td class='good'>Neutral, no pressure</td><td class='bad'>Urgent, threatening, too-good-to-be-true</td></tr>"
                + "</table>"
                + "<h2>&gt; 03. IF YOU EVER ENTER CREDENTIALS ON A SUSPECTED FAKE PAGE</h2>"
                + "<ol>"
                + "<li><strong>Change the password immediately</strong> on the real, official site</li>"
                + "<li><strong>Enable two-factor authentication</strong> if not already on</li>"
                + "<li><strong>Check for reused passwords</strong> on other accounts and change those too</li>"
                + "<li><strong>Report it</strong> to your institution's IT/security team</li>"
                + "</ol>"
                + "<a class='back' href='/'>&lt; Back to demo</a>"
                + "</div>"
                + "</body></html>";

            sendHtml(exchange, html);
        }
    }
}