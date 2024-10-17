//package co.rcprdn.lodgyserver.controller;
//
//import co.rcprdn.lodgyserver.payload.request.LoginRequest;
//import co.rcprdn.lodgyserver.repository.UserRepository;
//import co.rcprdn.lodgyserver.security.jwt.JwtUtils;
//import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AuthControllerTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @MockBean
//  private AuthenticationManager authenticationManager;
//
//  @MockBean
//  private UserRepository userRepository;
//
//  @MockBean
//  private JwtUtils jwtUtils;
//
//  private ObjectMapper objectMapper;
//
//  @BeforeEach
//  public void setup() {
//    objectMapper = new ObjectMapper();
//  }
//
//  @Test
//  public void testAuthenticateUser_success() throws Exception {
//    // Setup test data and mocks
//    LoginRequest loginRequest = new LoginRequest();
//    loginRequest.setPassword("12345678");
//    loginRequest.setUsername("rico");
//    UserDetailsImpl userDetails = new UserDetailsImpl(
//            1L,
//            "testuser",
//            "testuser@example.com",
//            "password",
//            List.of(new SimpleGrantedAuthority("ROLE_USER"))
//    );
//
//    // Mock authentication
//    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//    Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
//            .thenReturn(authentication);
//
//    // Mock JWT utility to generate a JWT cookie
//    ResponseCookie mockedCookie = ResponseCookie.from("jwtToken", "test.jwt.token")
//            .path("/api")
//            .httpOnly(true)
//            .build();
//
//    Mockito.when(jwtUtils.generateJwtCookie(userDetails))
//            .thenReturn(mockedCookie);
//
//    // Perform the request and assert the response
//    mockMvc.perform(post("/api/auth/signin")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(loginRequest)))
//            .andExpect(status().isOk())
//            .andExpect(header().string(HttpHeaders.SET_COOKIE, "jwtToken=test.jwt.token; Path=/api; HttpOnly"))
//            .andExpect(jsonPath("$.id").value(userDetails.getId()))
//            .andExpect(jsonPath("$.username").value(userDetails.getUsername()))
//            .andExpect(jsonPath("$.email").value(userDetails.getEmail()))
//            .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
//  }
//}
