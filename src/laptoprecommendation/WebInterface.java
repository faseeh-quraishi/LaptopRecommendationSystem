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
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.JsonParser;


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

            // Always allow CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            // Handle preflight request for CORS
            if (requestMethod.equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1); // No content
                return;
            }

            if (requestMethod.equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

                if (!jsonObject.has("method")) {
                    sendError(exchange, 400, "Method name is missing or null");
                    return;
                }

                String method = jsonObject.get("method").getAsString();
                JsonObject returnJsonObject = null;

                switch (method) {

                    case "spellCheck":
                        try {
                            String spelling = jsonObject.get("spelling").getAsString();
                            List<String> suggestions = Features.SpellCheck(spelling);
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
                            sendJson(exchange, jsonResponse);
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

                    case "increaseSearchFrequencyCount":
                        try {
                            String word = jsonObject.get("word").getAsString();
                            Map<String, Integer> updatedCounts = Features.addSearchedWordCount(word);

                            JsonObject resultObject = new JsonObject();
                            for (Entry<String, Integer> entry : updatedCounts.entrySet()) {
                                resultObject.addProperty(entry.getKey(), entry.getValue());
                            }

                            returnJsonObject = new JsonObject();
                            returnJsonObject.add("result", resultObject);

                        } catch (Exception e) {
                            sendError(exchange, 500, "Internal server error in increaseSearchFrequencyCount");
                            return;
                        }
                        break;

                    case "getTop5SearchedWords":
                        try {
                            List<Entry<String, Integer>> topWords = Features.getTop5SearchedWords();

                            JsonArray resultArray = new JsonArray();
                            for (Entry<String, Integer> entry : topWords) {
                                JsonObject entryObject = new JsonObject();
                                entryObject.addProperty("word", entry.getKey());
                                entryObject.addProperty("count", entry.getValue());
                                resultArray.add(entryObject);
                            }

                            returnJsonObject = new JsonObject();
                            returnJsonObject.add("result", resultArray);

                        } catch (Exception e) {
                            sendError(exchange, 500, "Internal server error in getTop5SearchedWords");
                            return;
                        }
                        break;

                    default:
                        sendError(exchange, 400, "Invalid method");
                        return;
                }

                String response = returnJsonObject.toString();
                sendJson(exchange, response);

            } else {
                sendError(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private static void sendJson(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        byte[] errorBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, errorBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(errorBytes);
        }
    }
}
