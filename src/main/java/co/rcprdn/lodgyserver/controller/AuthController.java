package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.enums.ERole;
import co.rcprdn.lodgyserver.payload.request.LoginRequest;
import co.rcprdn.lodgyserver.payload.request.SignupRequest;
import co.rcprdn.lodgyserver.payload.response.MessageResponse;
import co.rcprdn.lodgyserver.payload.response.UserInfoResponse;
import co.rcprdn.lodgyserver.repository.RoleRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.security.jwt.JwtUtils;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${user.approval.required:false}") // Default to false if not set
    private boolean userApprovalRequired;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        Long userId = userDetails.getId();
        String username = userDetails.getUsername();
        logger.info("User {} with ID {} logged in successfully.", username, userId);

        if (userApprovalRequired && !userDetails.isEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: User is not approved. Please contact an administrator.");
        }

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
    }

    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        user.setEnabled(false);

        userRepository.save(user);

        Long userId = user.getId();
        String username = user.getUsername();

        logger.info("User {} with ID {} registered successfully.", username, userId);

        if (userApprovalRequired) {// Create a LoginRequest to authenticate the newly created user
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(signUpRequest.getUsername());
            loginRequest.setPassword(signUpRequest.getPassword());


            // Call the authenticateUser method to log the user in
            return authenticateUser(loginRequest);
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageResponse("User is approved."));
        }
    }

    @PostMapping("/signout")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}
