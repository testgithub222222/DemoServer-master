package edu.csu2017fa314.DemoServer.Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.csu2017fa314.DemoServer.Database.QueryBuilder;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

import static spark.Spark.post;

/**
 * Created by sswensen on 10/1/17.
 */

public class Server {
    public static void main(String[] args) {
        Server s = new Server();
        s.serve();
    }

    public void serve() {
        Gson g = new Gson();
        post("/testing", this::testing, g::toJson); // Create new listener
    }

    private Object testing(Request rec, Response res) {
        // Set the return headers
        setHeaders(res);

        // Init json parser
        JsonParser parser = new JsonParser();

        // Grab the json body from POST
        JsonElement elm = parser.parse(rec.body());

        // Create new Gson (a Google library for creating a JSON representation of a java class)
        Gson gson = new Gson();

        // Create new Object from received JsonElement elm
        ServerRequest sRec = gson.fromJson(elm, ServerRequest.class);

        // The object generated by the frontend should match whatever class you are reading into.
        // Notice how DataClass has name and ID and how the frontend is generating an object with name and ID.
        System.out.println("Got \"" + sRec.toString() + "\" from server.");
        // Client sends query under "name" field in received json:
        String searched = sRec.getQuery();
        // Get something from the server
        QueryBuilder q = new QueryBuilder("mikelynn", "matthew1"); // Create new QueryBuilder instance and pass in credentials //TODO update credentials
        String queryString = String.format("SELECT * FROM airports WHERE municipality LIKE '%s' OR name LIKE '%s' OR type LIKE '%s' LIMIT 10", searched, searched, searched);
        ArrayList<Location> queryResults = q.query(queryString);

        // Create object with svg file path and array of matching database entries to return to server
        ServerResponse sRes = new ServerResponse("./testing.png", queryResults); //TODO update file path to your svg, change to "./testing.png" for a sample image

        System.out.println("Sending \"" + sRes.toString() + "\" to server.");

        //Convert response to json
        Object ret = gson.toJson(sRes, ServerResponse.class);

        /* What to return to the server.
         * In this example, the "ServerResponse" object sRes is converted into a JSON representation using the GSON library.
         * If you'd like to see what this JSON looks like, it is logged to the console in the web client.
         */
        return ret;
    }

    private void setHeaders(Response res) {
        // Declares returning type json
        res.header("Content-Type", "application/json");

        // Ok for browser to call even if different host host
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "*");
    }
}