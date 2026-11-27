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
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import com.cisneros_guevara_lara.SkyRoute.model.Cliente;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http,
                                                UsuarioRepository usuarioRepository,
                                                PasswordEncoder passwordEncoder) throws Exception {
        UserDetailsService soloAdministradores = correo -> {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .filter(u -> u instanceof Administrador)
                    .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + correo));
            return User.builder()
                    .username(usuario.getCorreo())
                    .password(usuario.getContrasenia())
                    .roles("ADMIN")
                    .build();
        };
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider(soloAdministradores);
        adminProvider.setPasswordEncoder(passwordEncoder);

        http
                .securityMatcher("/admin/**", "/admin")
                .authenticationManager(new ProviderManager(adminProvider))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("correo")
                        .passwordParameter("contrasenia")
                        .defaultSuccessUrl("/admin/reservaciones", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(request -> "/admin/logout".equals(request.getServletPath())
                                && "POST".equalsIgnoreCase(request.getMethod()))
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, e) ->
                                response.sendRedirect(request.getContextPath() + "/admin/login?denegado=true"))
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/imgs/**").permitAll()
                        .requestMatchers("/", "/login", "/registro", "/iniciar-sesion", "/inicio", "/vuelos", "/contacto", "/error").permitAll()
                        .requestMatchers("/reservar", "/asientos", "/pago", "/confirmar-reserva", "/reserva-confirmada")
                        .hasAnyRole("CLIENTE", "ADMIN")
                        .anyRequest().hasRole("CLIENTE")
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
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, e) ->
                                response.sendRedirect(request.getContextPath() + "/login?denegado=true"))
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
                    .filter(u -> u instanceof Cliente)
                    .orElseThrow(() -> new UsernameNotFoundException("Cliente no encontrado: " + correo));

            return User.builder()
                    .username(usuario.getCorreo())
                    .password(usuario.getContrasenia())
                    .roles("CLIENTE")
                    .build();
        };
    }
}
