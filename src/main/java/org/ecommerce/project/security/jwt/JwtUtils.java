package org.ecommerce.project.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.ecommerce.project.security.services.UserDetailsImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


    @Value("${spring.app.jwtExpirationMs}")    // This will be passed by application.properties file
    private int jwtExpirationMs; // If its equal to 300000 ms -> 5 mins, after this time the token will expire


    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;    // these values will be passed in by application.properties


    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;
    // This is the name of the jwt cookie, getting the value from application.properties




    // Getting the token from the header // This was when we are passing the bearer token everytime with the request, but we can use cookie instead, to avoid hassle of sending token everytime manually
    /*
    public String getJwtFromHeader(HttpServletRequest request){
        String tokenFromHeader = request.getHeader("Authorization");

        logger.debug("Authorization Header : {}", tokenFromHeader);

        if(tokenFromHeader !=null && tokenFromHeader.startsWith("Bearer ")){
            return tokenFromHeader.substring(7); // we are removing 'Bearer ' from the string, these are 7 units of length from start
        }
        return null;
    }
    */

    // Implementing cookie based jwt authentication
    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        // jwtCookie is the cookie name, we want to get this particular named cookie
        if(cookie!=null){
            return cookie.getValue();
        }
        else return null;
    }


    // Generating jwt token from cookie
    public ResponseCookie generateJwtCookie(UserDetailsImplementation userDetailsImplementation){
        String jwt = generateTokenFromUsername(userDetailsImplementation.getUsername());

        return ResponseCookie.from(jwtCookie, jwt)   // jwtCookie : cookie name, jwt : cookie value
                .path("/api")
                .httpOnly(false)
                .maxAge(24*60*60)
                .build();
    }


    // Generating clean cookie, this will override the cookie with already existing name jwtCookie on the browser with Null, so the user is signed out
    public ResponseCookie getCleanCookie(){
        ResponseCookie responseCookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
        return responseCookie;
    }



    // Generating token from the username
    public String generateTokenFromUsername(String username){
//        String username = userDetails.getUsername();
        return Jwts.builder()   // builder is there to build the token
                .subject(username) // setting the subject as username
                .issuedAt(new Date())  // new Date() object actually sets(refers) to the current time
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())  // signing the token with custom key function
                .compact();       // compact converts the content into JSON object and then encode header, payload and signature, and then concatenate them and convert all into a single jwt token string
    }

    // Getting username from jwt token
     public String getUsernameFromJWTToken(String jwtToken){
        return Jwts.parser() // parse is there to read the token
                .verifyWith((SecretKey) key()) // verifying the token with this key method
                .build().parseSignedClaims(jwtToken)   // build is use to build the parse // Claims -> data inside the jwt token, So parseSignedClaims -> parses the token and extract its claims
                .getPayload().getSubject();  // subject is usually the username
     }

    // Generating the signing key
    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecretKey)
        );
    }

    // Validating the JWT token
    public boolean validateJWTToken(String authToken){
        try{
            System.out.println("Validating");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }
        catch (MalformedJwtException exception){
            logger.error("Invalid JWT Token: {}", exception.getMessage());
        }
        catch (ExpiredJwtException exception){
            logger.error("JWT Token is expired: {}", exception.getMessage());
        }
        catch (UnsupportedJwtException exception){
            logger.error("JWT Token unsupported: {}", exception.getMessage());
        }
        catch (IllegalArgumentException exception){
            logger.error("String is empty: {}", exception.getMessage());
        }
        return false;
    }

}
