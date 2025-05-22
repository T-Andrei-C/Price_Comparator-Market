package main.service;

import lombok.RequiredArgsConstructor;
import main.model.user.Basket;
import main.model.user.TargetProduct;
import main.model.user.User;
import main.repository.BasketRepository;
import main.repository.ProductRepository;
import main.repository.TargetProductRepository;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TargetProductRepository targetProductRepository;
    private final ProductRepository productRepository;

    public String addUser (User user) {
        if (userRepository.findUserByEmail(user.getEmail()).orElse(null) == null) {
            user.setBasket(Basket.builder().build());
            userRepository.save(user);
            return "User added successfully";
        }

        return "User email is already in use";
    }

    public String addTargetProductToUser (String email, TargetProduct targetProduct) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null) {

            if (productRepository.findById(targetProduct.getProduct().getId()).isEmpty()){
                return "Product with id " + targetProduct.getProduct().getId() + " does not exist";
            }

            if (user.getTargetProduct() == null) {
                targetProduct.setUser(user);
                targetProductRepository.save(targetProduct);

                return "Target product added successfully";
            } else {
                TargetProduct userTargetProduct = targetProductRepository.findById(user.getTargetProduct().getId()).orElse(null);

                userTargetProduct.setProduct(targetProduct.getProduct());
                userTargetProduct.setExpectedPrice(targetProduct.getExpectedPrice());
                targetProductRepository.save(userTargetProduct);

                return "Target product replaced successfully";
            }
        }

        return "Target product couldn't be added";
    }

}
