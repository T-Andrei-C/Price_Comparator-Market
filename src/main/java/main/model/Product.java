package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String brand;
    private Double quantity;
    private String unit;

    @OneToMany(mappedBy = "product")
    List<Supermarket> supermarkets;

    @OneToMany(mappedBy = "product")
    List<SupermarketHistory> supermarketHistories;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(brand, product.brand) && Objects.equals(quantity, product.quantity) && Objects.equals(unit, product.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, brand, quantity, unit);
    }
}
