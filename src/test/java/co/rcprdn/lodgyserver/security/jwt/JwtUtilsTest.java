//package co.rcprdn.lodgyserver.security.jwt;
//
//import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@TestPropertySource(properties = {
//        "lodgy.app.jwtSecret=5c8538dba4b85acbcf1cf07e93518d73755218b08a4c308d9f24531899e7a26e",
//        "lodgy.app.jwtExpirationMs=86400000",  // Example: 1 day in milliseconds
//        "lodgy.app.jwtCookieName=jwtToken"
//})
//class JwtUtilsTest {
//
//  @Autowired
//  private JwtUtils jwtUtils;
//
//
//  @Test
//  public void testGenerateJwtCookie() {
//    UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "testuser@example.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
//
//    ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
//
//    // Validate cookie properties
//    assertTrue(cookie.getValue() != null && !cookie.getValue().isEmpty()); // Ensure the cookie value is a non-empty JWT
//
//    // Validate cookie attributes
//    assertEquals("/api", cookie.getPath());
//    assertTrue(cookie.isHttpOnly());
//
//    // Decode the JWT to validate its contents
//    String jwtToken = cookie.getValue();
//    Claims claims = Jwts.parserBuilder()
//            .setSigningKey(jwtUtils.key()) // Use the key method from JwtUtils to parse
//            .build()
//            .parseClaimsJws(jwtToken)
//            .getBody();
//
//    // Validate the claims in the JWT
//    assertEquals("testuser", claims.getSubject());
//    // You may also want to validate roles or other claims
//  }
//
//}