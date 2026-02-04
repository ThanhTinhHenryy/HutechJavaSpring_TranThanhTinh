package fit.hutech.spring.services;

import fit.hutech.spring.entities.User;
import fit.hutech.spring.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void save(@NotNull User user) {
        user.setUsername(user.getUsername() == null ? null : user.getUsername().trim());
        user.setEmail(user.getEmail() == null ? null : user.getEmail().trim().toLowerCase());
        user.setPhone(user.getPhone() == null ? null : user.getPhone().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        userRepository.save(user);
    }
    public Optional<User> findByUsername(String username) throws
            UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    public Optional<User> findByEmail(String email) throws
            UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByPhone(String phone) throws
            UsernameNotFoundException {
        return userRepository.findByPhone(phone);
    }
    public boolean existsByUsername(String username) {
        return username != null && userRepository.existsByUsername(username);
    }
    public boolean existsByEmail(String email) {
        return email != null && userRepository.existsByEmail(email);
    }
    public boolean existsByPhone(String phone) {
        return phone != null && userRepository.existsByPhone(phone);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        String input = username == null ? null : username.trim();
        if (input == null || input.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        Optional<User> byUsername = userRepository.findByUsername(input);
        if (byUsername.isPresent()) return byUsername.get();
        Optional<User> byEmail = userRepository.findByEmail(input.toLowerCase());
        if (byEmail.isPresent()) return byEmail.get();
        Optional<User> byPhone = userRepository.findByPhone(input);
        return byPhone.orElseThrow(() ->
                new UsernameNotFoundException("User not found with identifier: " + username)
        );
    }
}
