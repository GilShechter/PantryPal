package com.pantrypal.pantrypal.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private DBUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager am;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try{
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final DBUser user = userDetailsService.getUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token, user.getId()));
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody JwtRequest userRequest) throws Exception {
        String encodedPass = passwordEncoder.encode(userRequest.getPassword());
        if (userService.findUserName(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        DBUser user = DBUser.UserBuilder.anUser().name(userRequest.getUsername())
                .password(encodedPass).build();
        userService.save(user);
        UserDetails userDetails = new User(userRequest.getUsername(), encodedPass, new ArrayList<>());
        return ResponseEntity.ok(new JwtResponse(jwtTokenUtil.generateToken(userDetails), user.getId()));
    }

    @RequestMapping(value = "{username}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            userService.deleteByUserName(username);
            return ResponseEntity.ok("User: " + username + " deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User: " + username + " not found");
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("User deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
