package mapreduce.app.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mapreduce.app.utilities.Enums.AppUserRoles;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @Size(min=4, max=255, message="Minimum 4 letter and maximum 255 letter username")
    @Column(nullable=false, unique=true)
    String username;

    @Column(unique=true, nullable=false)
    String email;

    @Size(min=8, message="Minimum 8 password length")
    String hashedPassword;

    @ElementCollection(fetch=FetchType.EAGER)
    List<AppUserRoles> appUserRoles;
}
