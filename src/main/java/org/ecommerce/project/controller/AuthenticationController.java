package org.ecommerce.project.controller;

import jakarta.validation.Valid;
import org.ecommerce.project.metrics.MetricsService;
import org.ecommerce.project.model.AppRoles;
import org.ecommerce.project.model.Role;
import org.ecommerce.project.model.User;
import org.ecommerce.project.repositories.RoleRepository;
import org.ecommerce.project.repositories.UserRepository;
import org.ecommerce.project.security.jwt.JwtUtils;
import org.ecommerce.project.security.request.LoginRequest;
import org.ecommerce.project.security.request.SignupRequest;
import org.ecommerce.project.security.response.MessageResponse;
import org.ecommerce.project.security.response.UserInfoResponse;
import org.ecommerce.project.security.services.UserDetailsImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private MetricsService metricsService;

    @PostMapping("/signin")
    public ResponseEntity<?> authentication(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("Status" , false);
            // login failed
            metricsService.loginFailure();

            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
        //this is the custom UserDetails implementation

        // We need to get jwt token, username and roles from the userDetails and then send them as the response
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
        // This is getting the role from every item inside the List

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), jwtCookie.getValue(), roles); // This is the constructor from UserDetailsImplementation class that we have defined

        // login is successful
        metricsService.loginSuccess();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest){
        if(userRepository.existsUserByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Username already Taken"));
        }

        if(userRepository.existsUserByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Email is already taken"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );


        // This roleStr can be empty as well, as its NOT mandatory for the user to pass in the roles, Its not defined in the model

        Set<String> roleStr = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if(roleStr==null){ // if role is not passed by user, then we set the default role here
            Role userRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_USER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                roles.add(userRole);
        }
        else{ // user passed in some roles through the SignupRequest DTO
                roleStr.forEach(role -> {
                    if(role.toLowerCase().contains("admin")){
                        Role adminRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(adminRole);
                    }
                    else if(role.toLowerCase().contains("seller")){
                        Role sellerRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_SELLER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(sellerRole);
                    }

                    // If user sends role as user, or any other role which is not valid, then by default it will be set to the user role, therefore this else block

                    else  {
                        Role userRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_USER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(userRole);
                    }
                });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User Registered Successfully"));
    }

    // Getting the name of the current user authenticated, so we can display this name in the header of the website, next to the profile photo, we can display this username
    @GetMapping("/user/currentUser/username")
    public String currentUser_username(Authentication authentication){
        if(authentication!=null) {
            return authentication.getName();
        }
        else return "";
    }


    // Getting the details of the current authenticated user
    @GetMapping("/user/currentUser/details")
    public ResponseEntity<?> currentUser_details(Authentication authentication){
        UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
        // .getPrincipal() gives the details of the user, and we are casting it into UserDetailsImplementation type

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
        // This is getting the role from every item inside the List

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles); // This is the constructor from UserDetailsImplementation class that we have defined

        return ResponseEntity.ok().body(response);
    }


    // It returns a cookie that doesnt contain JWt token in it (clean cookie)
    @PostMapping("/signout")
    public ResponseEntity<?> signinOutUser(){
        ResponseCookie responseCookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(new MessageResponse("You have been signed out"));
    }
}




