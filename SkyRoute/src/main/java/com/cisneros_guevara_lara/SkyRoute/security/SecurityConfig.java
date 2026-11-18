package com.cisneros_guevara_lara.SkyRoute.security;

import com.cisneros_guevara_lara.SkyRoute.model.Administrador;
import com.cisneros_guevara_lara.SkyRoute.model.Usuario;
import com.cisneros_guevara_lara.SkyRoute.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/imgs/**").permitAll()
                        .requestMatchers("/", "/login", "/registro", "/iniciar-sesion", "/inicio", "/vuelos", "/contacto").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                     // Coincide con @GetMapping({"/", "/login"})
                        .loginProcessingUrl("/iniciar-sesion")   // Intercepta el POST del formulario de index.html
                        .usernameParameter("correo")             // Ajusta si en index.html el input tiene otro name
                        .passwordParameter("contrasenia")        // Ajusta si en index.html el input tiene otro name
                        .defaultSuccessUrl("/inicio", true)      // Redirige a /inicio (si fuera "/" volvería al login)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(request -> "/logout".equals(request.getServletPath())
                                && ("GET".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod())))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return correo -> {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

            String rol = (usuario instanceof Administrador) ? "ADMIN" : "CLIENTE";

            return User.builder()
                    .username(usuario.getCorreo())
                    .password(usuario.getContrasenia())
                    .roles(rol)
                    .build();
        };
    }
}
