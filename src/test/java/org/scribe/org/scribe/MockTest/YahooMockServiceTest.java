package org.scribe.org.scribe.MockTest;

/**
 * Created by kailashbysani on 4/25/14.
 */

import static org.junit.Assert.assertEquals;

import java.util.Map;


import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;

import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth10aServiceImpl;


public class YahooMockServiceTest {


    private OAuth10aServiceImpl builder;
    private static final String PROTECTED_RESOURCE_URL = "http://social.yahooapis.com/v1/user/A6ROU63MXWDCW3Y5MGCYWVHDJI/profile/status?format=json";
    private Token requestToken;
    private Verifier verifier;

    @Before
    public void setup() {
        builder = (OAuth10aServiceImpl) new ServiceBuilder()
                .provider(YahooApi.class)
                .apiKey("dj0yJmk9TXZDWVpNVVdGaVFmJmQ9WVdrOWMweHZXbkZLTkhVbWNHbzlNVEl5TWprd05qUTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0wMw--")
                .apiSecret("262be559f92a2be20c4c039419018f2b48cdfce9").build();
        verifier = new Verifier("someString");

    }

    @Test
    public void checkYahooRequestTokenTest() {
        requestToken = builder.getRequestToken();

        OAuthRequest req = builder.getRequest();
        String url = req.getSanitizedUrl();
        Map<String, String> reqParameters = req.getOauthParameters();
        String scopeStr = reqParameters.get("scope");
        assertEquals("https://api.login.yahoo.com/oauth/v2/get_request_token",
                url);
        // assertEquals(scopeStr, "https://docs.google.com/feeds/");
    }

    @Test
    public void yahooGetAccessToketTest() {
        requestToken = builder.getRequestToken();
        GoogleConnectStub connectStub = new GoogleConnectStub(
                Verb.GET,
                "http://social.yahooapis.com/v1/user/A6ROU63MXWDCW3Y5MGCYWVHDJI/profile/status?format=json");
        // replay(builder);
        Token accessToken = builder.getAccessToken(requestToken, verifier);

        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: "
                + accessToken + " )");
        assertEquals("KEy", accessToken.getSecret());
        assertEquals("Secret", accessToken.getToken());

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET,
                "http://social.yahooapis.com/v1/user/A6ROU63MXWDCW3Y5MGCYWVHDJI/profile/status?format=json");
        builder.signRequest(accessToken, request);
        connectStub.addHeader("GData-Version", "3.0");
        Response response = connectStub.send();
        System.out.println();
        assertEquals(200, response.getCode());
        System.out.println(response.getBody());
    }



}
