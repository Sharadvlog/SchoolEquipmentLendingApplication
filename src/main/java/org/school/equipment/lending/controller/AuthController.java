package org.school.equipment.lending.controller;

import lombok.RequiredArgsConstructor;
import org.school.equipment.lending.entity.User;
import org.school.equipment.lending.model.UserSignUpRequestDTO;
import org.school.equipment.lending.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final UserService userService;

    @PostMapping(path = "/signup",consumes = "application/json")
    public ResponseEntity signUp(@RequestBody UserSignUpRequestDTO userRequest) {
        User savedUser = userService.signUp(userRequest);
        return ResponseEntity.ok().build();
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credential){
        String username = credential.get("username");
        String password = credential.get("password");
        Optional<User> user = userService.login(username, password);
        if(user.isPresent()){
            String token = user.get().getRole()+"-"+user.get().getUsername();
            Map<String, String> response= Map.of(
                    "token", token,
                    "role", user.get().getRole(),
                    "username", user.get().getUsername()
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credential");
    }

    
}
