package net.javaguides.sms.config;

import net.javaguides.sms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/register/**", "/index").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/teacher/**").hasRole("TEACHER")
                        .antMatchers("/student/**").hasRole("STUDENT")
                        .antMatchers("/courses/**").hasAnyRole("TEACHER","STUDENT")
                        .antMatchers("/auth/register").permitAll() // Allow registration
                        .antMatchers("/auth/login").permitAll() // Allow login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login") // URL where the login form is posted
                        .defaultSuccessUrl("/", true) // Default redirect after successful login for other roles
                        .successHandler((request, response, authentication) -> {
                            // Redirect based on user role
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_TEACHER"))) {
                                response.sendRedirect("/courses");
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_STUDENT"))) {
                                response.sendRedirect("/student/courses");
                            } else {
                                response.sendRedirect("/"); // Default redirect for other roles
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
