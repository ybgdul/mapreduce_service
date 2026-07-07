package mapreduce.app.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.AppUser;
import mapreduce.app.repositories.AppUserRepo;
import mapreduce.app.utilities.DTOs.UserRequestDto;
import mapreduce.app.utilities.DTOs.UserResponseDto;
import mapreduce.app.utilities.Enums.AppUserRoles;
import mapreduce.app.utilities.Exceptions.CustomAuthException;
import mapreduce.app.utilities.Exceptions.UserAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class AppUserService {
    
    private final AppUserRepo userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwtService;
    private static Logger log = LoggerFactory.getLogger(AppUserService.class);
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UserResponseDto signup(UserRequestDto request) { 
        
        if(userRepo.existsByUsername(request.username())) throw new UserAlreadyExistsException("User already exists: " + request.username());

        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setHashedPassword(encoder.encode(request.password()));
        user.setAppUserRoles(List.of(AppUserRoles.USER));

        userRepo.saveAndFlush(user);

        String token = jwtService.generateToken(new User(user.getUsername(), user.getHashedPassword(), user.getAppUserRoles()));

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    @Transactional
    public UserResponseDto login(UserRequestDto request) { 

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AppUser user = userRepo.findByUsername(request.username()).orElseThrow(() -> new UsernameNotFoundException("Username not found: " + request.username()));

        String token = jwtService.generateToken(new User(user.getUsername(), user.getHashedPassword(), user.getAppUserRoles()));

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    @Transactional(readOnly = true)
    public AppUser whoami(HttpServletRequest req) { 
        String token = jwtService.resolveToken(req);

        if(token == null) throw new CustomAuthException("Missing Authentication token", HttpStatus.UNAUTHORIZED);

        AppUser user = userRepo.findByUsername(jwtService.getUsername(token)).orElseThrow(() -> new CustomAuthException("User doesn't exist", HttpStatus.NOT_FOUND));
        
        return user;
    }  

    @Transactional(readOnly = true)
    public AppUser search(String username) { 
        return userRepo.findByUsername(username).orElseThrow(() -> new CustomAuthException("User doesn't exist", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void delete(String username) { 
        userRepo.deleteByUsername(username);
    }

    public String refresh(String username) { 

        if(!userRepo.existsByUsername(username)) throw new CustomAuthException("User doesn't exist", HttpStatus.NOT_FOUND);

        return jwtService.generateToken(userDetailsService.loadUserByUsername(username));
    }




}
