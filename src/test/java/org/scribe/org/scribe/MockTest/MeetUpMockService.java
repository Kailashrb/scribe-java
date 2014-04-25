package org.scribe.org.scribe.MockTest;

/**
 * Created by kailashbysani on 4/25/14.
 */

import java.util.Map;


import org.junit.Before;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.MeetupApi;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;

public class MeetUpMockService {


    private OAuth10aServiceImpl builder;
    private static final String PROTECTED_RESOURCE_URL = "http://api.meetup.com/2/member/self";
    private Token requestToken;
    private Verifier verifier;

    @Before
    public void setup() {
        builder = (OAuth10aServiceImpl) new ServiceBuilder()
                .provider(MeetupApi.class)
                .apiKey("j1khkp0dus323ftve0sdcv6ffe")
                .apiSecret("6s6gt6q59gvfjtsvgcmht62gq4").build();
        verifier = new Verifier("someString");

    }

    @Test
    public void checkMeetUpRequestTokenTest() {
        requestToken = builder.getRequestToken();

        OAuthRequest req = builder.getRequest();
        String url = req.getSanitizedUrl();
        Map<String, String> reqParameters = req.getOauthParameters();
        assertEquals("http://api.meetup.com/oauth/request/",
                url);
    }

    @Test
    public void meetUpGetAccessToketTest() {
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
