package mapreduce.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mapreduce.app.entities.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser, Long>{
    
    public Optional<AppUser> findByUsername(String username);

    public boolean existsByUsername(String username);

    public void deleteByUsername(String username);
}
