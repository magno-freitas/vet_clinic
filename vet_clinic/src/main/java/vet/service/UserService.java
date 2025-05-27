package vet.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vet.model.User;

@Service
public interface UserService extends UserDetailsService {
    User registerNewUser(User user);
    boolean isEmailAlreadyInUse(String email);
}