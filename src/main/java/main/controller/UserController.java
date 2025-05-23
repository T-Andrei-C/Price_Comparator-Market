package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.user.TargetProduct;
import main.model.user.User;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/upload")
    private ResponseEntity<String> addUser (@RequestBody User user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PostMapping("/user/email/{email}/target")
    private ResponseEntity<String> addTargetProductToUser (@PathVariable String email, @RequestBody TargetProduct targetProduct) {
        return ResponseEntity.ok(userService.addTargetProductToUser(email, targetProduct));
    }

}
