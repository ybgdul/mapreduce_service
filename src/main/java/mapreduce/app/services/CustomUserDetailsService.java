package mapreduce.app.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.AppUser;
import mapreduce.app.repositories.AppUserRepo;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
    
    private final AppUserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        AppUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        return User.withUsername(username).accountExpired(false).accountLocked(false).authorities(user.getAppUserRoles()).credentialsExpired(false).password(user.getHashedPassword()).disabled(false).build();
    }
    
}
