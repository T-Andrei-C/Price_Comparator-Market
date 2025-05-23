package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.model.user.Basket;
import main.model.user.TargetProduct;
import main.model.user.User;
import main.repository.ProductRepository;
import main.repository.TargetProductRepository;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TargetProductRepository targetProductRepository;
    private final ProductRepository productRepository;

    public String addUser(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).orElse(null) == null) {
            user.setBasket(Basket.builder().build());
            userRepository.save(user);
            return "User added successfully";
        }

        return "User email is already in use";
    }

    public String addTargetProductToUser(String email, TargetProduct targetProduct) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        Product product = productRepository.findById(targetProduct.getProduct().getId()).orElse(null);

        if (product == null) {
            return "Product with id " + targetProduct.getProduct().getId() + " does not exist";
        }

        if (checkIfProductPriceIsBelowOrEqualToTheTargetProduct(product, targetProduct.getExpectedPrice())) {
            return "Product " + product.getName() + " is already below or equal to the expected price";
        }

        if (user != null) {
            if (user.getTargetProduct() == null) {
                targetProduct.setUser(user);
                targetProductRepository.save(targetProduct);

                return "Target product added successfully";
            } else {
                TargetProduct userTargetProduct = targetProductRepository.findById(user.getTargetProduct().getId()).orElse(null);

                if (userTargetProduct != null) {
                    userTargetProduct.setProduct(targetProduct.getProduct());
                    userTargetProduct.setExpectedPrice(targetProduct.getExpectedPrice());
                    targetProductRepository.save(userTargetProduct);

                    return "Target product replaced successfully";
                }
            }
        }

        return "Target product couldn't be added";
    }

    private boolean checkIfProductPriceIsBelowOrEqualToTheTargetProduct(Product product, Double expectedPrice) {
        LocalDate simulateDate = SimulateDate.getDate();

        for (Supermarket supermarket : product.getSupermarkets()) {

            if (supermarket.getProduct_price() <= expectedPrice) {
                return true;
            }

            Discount discount = supermarket.getDiscount();

            if (discount != null && DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
                if (
                        DiscountCalculator.applyDiscountToProductPrice(
                                supermarket.getProduct_price(), discount.getPercentage_of_discount()
                        ) <= expectedPrice
                ) {
                    return true;
                }
            }
        }

        return false;
    }
}
