package com.wds.security.apple;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevTokenGeneratorTest {

    public static final String algorithm = "ES256";

    @Test
    public void generateDeveloperToken() {

        String issuerTeamId = "ITID1";
        String kid = "KEYID2";
        String issuer = "apple";

        TokenGenerator tg = new TokenGenerator();
        String token = tg.generateDeveloperTokenForECDSA256(issuer,issuerTeamId, kid, 90);

        DecodedJWT jwt = JWT.decode(token);
        assertThat(jwt.getAlgorithm(), is(algorithm));
        assertThat(jwt.getKeyId(), is(kid));
        assertThat(jwt.getIssuer(), is(issuerTeamId));
        assertTrue(jwt.getExpiresAt().after(Date.from(Instant.now())));
        assertThat(new String(Base64.getDecoder().decode(jwt.getHeader())),
                is(String.format("{\"alg\":\"%s\",\"typ\":\"JWT\",\"kid\":\"%s\"}", algorithm, kid)));

    }
}
