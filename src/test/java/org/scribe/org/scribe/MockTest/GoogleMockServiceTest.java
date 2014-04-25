package org.scribe.org.scribe.MockTest;

/**
 * Created by kailashbysani on 4/25/14.
 */


import static org.junit.Assert.*;
import java.util.Map;
import org.junit.*;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth10aServiceImpl;
public class GoogleMockServiceTest {

    private OAuth10aServiceImpl builder;
    private static final String SCOPE = "https://docs.google.com/feeds/";
    private Token requestToken;
    private Verifier verifier;

    @Before
    public void setup() {
        builder = (OAuth10aServiceImpl) new ServiceBuilder()
                .provider(GoogleApi.class).apiKey("anonymous")
                .apiSecret("anonymous").scope(SCOPE).build();
        verifier = new Verifier("someString");

    }

    @Test
    public void checkGoogleRequestTokenTest() {
        requestToken = builder.getRequestToken();
        OAuthRequest req = builder.getRequest();
        String url = req.getSanitizedUrl();
        Map<String, String> reqParameters = req.getOauthParameters();
        String scopeStr = reqParameters.get("scope");
        assertEquals(url,
                "https://www.google.com/accounts/OAuthGetRequestToken");
        assertEquals(scopeStr, "https://docs.google.com/feeds/");
    }

    @Test
    public void googleGetAccessToketTest() {
        requestToken = builder.getRequestToken();
        GoogleConnectStub connectStub = new GoogleConnectStub(Verb.GET, "https://docs.google.com/feeds/default/private/full/");
//		replay(builder);
        Token accessToken = builder.getAccessToken(requestToken, verifier);

        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: "
                + accessToken + " )");
        assertEquals("KEy",accessToken.getSecret());
        assertEquals("Secret",accessToken.getToken());

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET,
                "https://docs.google.com/feeds/default/private/full/");
        builder.signRequest(accessToken, request);
        connectStub.addHeader("GData-Version", "3.0");
        Response response = connectStub.send();
        System.out.println();
        assertEquals(200,response.getCode());
        System.out.println(response.getBody());
    }


}
