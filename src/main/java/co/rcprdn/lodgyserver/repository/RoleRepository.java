package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
