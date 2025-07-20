package laptoprecommendation;

import laptoprecommendation.Features;
import pageRanking.Laptop;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;

public class WebInterface {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // Changed from 8080 to 8081
        server.createContext("/WebApi", new ApiHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080/WebApi");
    }

    static class ApiHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject jsonObject = com.google.gson.JsonParser.parseString(requestBody).getAsJsonObject();

                if (!jsonObject.has("method")) {
                    sendError(exchange, 400, "Method name is missing or null");
                    return;
                }

                String method = jsonObject.get("method").getAsString();
                JsonObject returnJsonObject = null;

                switch (method) {

                    case "spellCheck":
                        try {
                            String spellings = jsonObject.get("spelling").getAsString();
                            List<String> suggestions = Features.SpellCheck(spellings);
                            JsonArray resultArray = new JsonArray();
                            for (String suggestion : suggestions) {
                                resultArray.add(suggestion);
                            }
                            returnJsonObject = new JsonObject();
                            returnJsonObject.add("result", resultArray);
                        } catch (Exception e) {
                            sendError(exchange, 500, "Internal server error in spellCheck");
                            return;
                        }
                        break;

                    case "SearchProduct":
                        try {
                            String spellings = jsonObject.get("spelling").getAsString();
                            List<Laptop> laptops = Features.SearchProduct(spellings);
                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(laptops);

                            exchange.getResponseHeaders().add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(jsonResponse.getBytes());
                            }
                            return;
                        } catch (Exception e) {
                            sendError(exchange, 500, "Internal server error in SearchProduct");
                            return;
                        }

                    case "WordCompletion":
                        try {
                            String prefix = jsonObject.get("prefix").getAsString();
                            List<String> completions = Features.WordCompletion(prefix);
                            JsonArray resultArray = new JsonArray();
                            for (String suggestion : completions) {
                                resultArray.add(suggestion);
                            }
                            returnJsonObject = new JsonObject();
                            returnJsonObject.add("result", resultArray);
                        } catch (Exception e) {
                            sendError(exchange, 500, "Internal server error in WordCompletion");
                            return;
                        }
                        break;

                    default:
                        sendError(exchange, 400, "Invalid method");
                        return;
                }

                String response = returnJsonObject.toString();
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                sendError(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
