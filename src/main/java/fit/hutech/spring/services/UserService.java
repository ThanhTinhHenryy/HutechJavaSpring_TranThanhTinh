package fit.hutech.spring.services;

import fit.hutech.spring.constants.Provider;
import fit.hutech.spring.entities.User;
import fit.hutech.spring.repositories.IRoleRepository;
import fit.hutech.spring.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private IRoleRepository roleRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void save(@NotNull User user) {
        user.setUsername(user.getUsername() == null ? null : user.getUsername().trim());
        user.setEmail(user.getEmail() == null ? null : user.getEmail().trim().toLowerCase());
        user.setPhone(user.getPhone() == null ? null : user.getPhone().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        userRepository.save(user);
        setDefaultRole(user.getUsername());
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
    public void saveOauthUser(String email, String name) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        if (normalizedEmail == null || normalizedEmail.isEmpty()) return;
        if (userRepository.existsByEmail(normalizedEmail)) return;
        User user = new User();
        user.setUsername(normalizedEmail);
        user.setEmail(normalizedEmail);
        user.setPhone(null);
        user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
        user.setProvider("oauth");
        userRepository.save(user);
        setDefaultRole(user.getUsername());
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

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresent(u -> {
            var roleUser = roleRepository.findByName("ROLE_USER");
            if (roleUser == null) {
                var r = new fit.hutech.spring.entities.Role();
                r.setName("ROLE_USER");
                r.setDescription("Default user role");
                roleUser = roleRepository.save(r);
            }
            u.getRoles().add(roleUser);
            userRepository.save(u);
        });
    }

//    public void saveOauthUser(String email, @NotNull String username) {
//        if(userRepository.findByUsername(username).isPresent())
//            return;
//        var user = new User();
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPassword(new BCryptPasswordEncoder().encode(username));
//        user.setProvider(Provider.GOOGLE.value);
//        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
//        userRepository.save(user);
//    }
}
