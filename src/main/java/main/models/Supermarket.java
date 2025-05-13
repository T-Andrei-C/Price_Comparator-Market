package main.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supermarkets")
public class Supermarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate publish_date;
    private Long product_price;
    private String currency;

    @JsonManagedReference
    @ManyToMany(mappedBy = "supermarkets")
    private Set<Product> products;

    @OneToMany(mappedBy = "supermarket")
    private Set<Discount> discounts;

}
