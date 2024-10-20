package co.rcprdn.lodgyserver.entity;

import co.rcprdn.lodgyserver.enums.ERole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role() { }

    public Role(ERole name) {
        this.name = name;
    }

    public Role(String name) {
        this.name = ERole.valueOf(name);
    }

    public static Role fromString(String roleName) {
        return new Role(ERole.valueOf(roleName));
    }

}
