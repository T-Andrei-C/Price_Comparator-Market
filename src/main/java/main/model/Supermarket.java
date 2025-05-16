package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "supermarkets")
public class Supermarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate publish_date;
    private Double product_price;
    private String currency;

    @OneToMany(mappedBy = "supermarket")
    private Set<Discount> discounts;

    @ManyToOne
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Supermarket that = (Supermarket) o;
        return Objects.equals(name, that.name) && Objects.equals(currency, that.currency) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, currency, product);
    }
}
