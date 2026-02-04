package fit.hutech.spring.utils;

import fit.hutech.spring.services.UserService;
import fit.hutech.spring.services.OauthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(
            @NotNull HttpSecurity http,
            UserDetailsService userDetailsService,
            UserService userService,
            OauthService oAuthService
    ) throws Exception {
        return http.authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/css/**", "/js/**", "/",
                                "/register", "/error")
                        .permitAll()
                        .requestMatchers( "/books/edit",
                                "/books/delete")
                        .authenticated()
                        .requestMatchers("/books", "/books/add")
                        .authenticated()
                        .requestMatchers("/api/**")
                        .authenticated()
                        .anyRequest().authenticated()
                )
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .permitAll()
                )
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.loginPage("/login")
                                .failureUrl("/login?error=true")
                                .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                                .successHandler((request, response, authentication) -> {
                                    Object principal = authentication.getPrincipal();
                                    if (principal instanceof OidcUser oidcUser) {
                                        String email = oidcUser.getEmail();
                                        String name = oidcUser.getFullName();
                                        userService.saveOauthUser(email, name);
                                    }
                                    response.sendRedirect("/");
                                })
                )
                .rememberMe(rememberMe ->
                        rememberMe.key("hutech").rememberMeCookieName("hutech")
                                .tokenValiditySeconds(24 * 60 * 60)
                                .userDetailsService(userDetailsService)
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedPage("/403"))
                .sessionManagement(sessionManagement ->
                        sessionManagement.maximumSessions(1)
                                .expiredUrl("/login")
                )
                .build();
    }
}
