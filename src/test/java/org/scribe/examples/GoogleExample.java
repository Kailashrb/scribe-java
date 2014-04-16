package org.scribe.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

public class GoogleExample {
    private static final String NETWORK_NAME = "Google";
    private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
    private static final String PROTECTED_RESOURCE_URL = "https://docs.google.com/feeds/default/private/full/";
    private static final String SCOPE = "https://docs.google.com/feeds/";

    public static void main(String[] args) throws Exception {
        OAuthService service = new ServiceBuilder()
                .provider(GoogleApi.class)
                .apiKey("anonymous")
                .apiSecret("anonymous")
                .scope(SCOPE)
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println("(if your curious it looks like this: " + requestToken + " )");
        System.out.println();

        System.out.println("Now go and authorize Scribe here:");
        System.out.println(AUTHORIZE_URL + requestToken.getToken());
        // String url = AUTHORIZE_URL+requestToken.getToken();
        String url = AUTHORIZE_URL + requestToken.getToken();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        //con.connect();
        String responseObj = con.getContent().toString();
        //System.out.println(responseObj);
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader inn = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response1 = new StringBuffer();

        while ((inputLine = inn.readLine()) != null) {
            response1.append(inputLine);
        }
        inn.close();

        //print result
        System.out.println(response1.toString());


        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(requestToken, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        request.addHeader("GData-Version", "3.0");
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");

    }


}