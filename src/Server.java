import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class Server {

    public static void main(String[] args) throws IOException {

        // Create a server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // When someone visits "/", serve the fake login page
        server.createContext("/", new LoginPageHandler());

        // When the login form is submitted, handle it (simulated only)
        server.createContext("/submit", new SubmitHandler());

        server.setExecutor(null); // use the default single-threaded executor
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }

    // Handler 1: serves the login.html file
    static class LoginPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File("public/login.html");
            byte[] bytes = Files.readAllBytes(file.toPath());

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // Handler 2: responds to form submission WITHOUT capturing real data
    static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body style='font-family:sans-serif; padding:40px;'>"
                + "<h2>⚠️ This was a simulated phishing demo</h2>"
                + "<p>In a real attack, whatever you typed would have just been "
                + "silently captured and logged by the attacker's server — "
                + "you'd never see this warning.</p>"
                + "<p>Example of what an attacker's log file might contain:</p>"
                + "<pre>2026-07-11 14:32:07 | username: john_doe | password: ********</pre>"
                + "<p><a href='/'>Go back</a></p>"
                + "</body></html>";

            byte[] bytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}