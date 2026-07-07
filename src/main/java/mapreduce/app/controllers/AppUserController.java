package mapreduce.app.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mapreduce.app.services.AppUserService;
import mapreduce.app.utilities.DTOs.UserRequestDto;
import mapreduce.app.utilities.DTOs.UserResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AppUserController {
    
    private final AppUserService userService;
    private final ModelMapper mapper;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid UserRequestDto request) { 
        
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody @Valid UserRequestDto request) { 
        
        return ResponseEntity.ok(userService.signup(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> whoami(HttpServletRequest req) { 
        UserResponseDto response = mapper.map(userService.whoami(req), UserResponseDto.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value="/delete/{username}")
    public ResponseEntity<?> delete(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(username);
    }

    @GetMapping(value="/search/{username}")
    public ResponseEntity<UserResponseDto> search(@PathVariable String username) {
        return ResponseEntity.ok(mapper.map(userService.search(username), UserResponseDto.class));
    }

    @GetMapping(value="/refresh/{username}")
    public ResponseEntity<String> refresh(@PathVariable String username) {
        return ResponseEntity.ok(userService.refresh(username));
    }

}
