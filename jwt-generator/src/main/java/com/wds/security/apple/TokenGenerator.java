package com.wds.security.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenGenerator {

    /**
     * Generate an encoded developer token
     * @param issuer - the issuer of the token ("apple")
     * @param issuerTeamId - a 10-character Team ID, obtained from the developer account
     * @param kid - a 10-character key identifier of the private key, obtained from the developer account
     * @param daysValid - number days for which this token will be valid
     * @return encoded developer token
     */
    public String generateDeveloperTokenForECDSA256(String issuer, String issuerTeamId, String kid, int daysValid) {

        Instant issuedAt = Instant.now();
        LocalDate date = issuedAt.atZone(ZoneId.systemDefault()).toLocalDate();
        Date expiresAt = Date.from(date.plusDays(daysValid).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String token;

        try {
            PrivateKey privateKey = getPrivateKey(issuer);
            Algorithm algorithm = Algorithm.ECDSA256(null, (ECPrivateKey)privateKey);
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "ES256");
            header.put("kid", kid);
            token = JWT.create()
                    .withHeader(header)
                    .withIssuer(issuerTeamId)
                    .withIssuedAt(java.util.Date.from(issuedAt))
                    .withExpiresAt(expiresAt).sign(algorithm);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return token;
    }

    protected PrivateKey getPrivateKey(String issuer) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String filePath = String.format("/keys/%s.p8", issuer);
        InputStream in = getClass().getResourceAsStream(filePath);
        byte[] privateKeyByteArray = clean(IOUtils.toByteArray(in));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        return keyFactory.generatePrivate(keySpec);
    }

    private byte[] clean(byte[] key) {
        String privateKeyPEM = new String(key);
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("\n-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        return Base64.getDecoder().decode(privateKeyPEM);
    }
}
