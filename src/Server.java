import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new FileHandler("public/login.html"));
        server.createContext("/submit", new SubmitHandler());
        server.createContext("/submit.html", new RedirectHandler("/feed"));
        server.createContext("/feed", new FileHandler("public/feed.html"));
        server.createContext("/awareness", new FileHandler("public/awareness.html"));
        server.createContext("/login.html", new RedirectHandler("/"));

        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }

    static class FileHandler implements HttpHandler {
        private final String filePath;

        FileHandler(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File(filePath);
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Location", "/feed");
                exchange.sendResponseHeaders(302, 0);
            } else {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, 0);
            }
            exchange.close();
        }
    }

    static class RedirectHandler implements HttpHandler {
        private final String target;

        RedirectHandler(String target) {
            this.target = target;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Location", target);
            exchange.sendResponseHeaders(302, 0);
            exchange.close();
        }
    }
}
//java -cp src Server