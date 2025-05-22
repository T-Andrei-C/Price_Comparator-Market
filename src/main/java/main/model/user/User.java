package main.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private Basket basket;

    @OneToOne(mappedBy = "user")
    private TargetProduct targetProduct;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;
}
