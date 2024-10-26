package com.kamal.scm_app.config;

import com.kamal.scm_app.service.SecurityCustomUserDetailsService;
import com.kamal.scm_app.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
//    @Bean
//    public UserDetailsService userDetailsService(){
//        //creating a inMemory temporary user to log in
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        UserDetails user = User.withUsername("admin")
//                .password(encoder.encode("123456"))
//                .roles("ADMIN","USER")
//                .build();
//        var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user);
//        return inMemoryUserDetailsManager;
//    }

    //getting db users using dao auth provider

    @Autowired
    private SecurityCustomUserDetailsService securityCustomUserDetailsService;

    @Autowired
    private OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securityCustomUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> {
//            authorize.requestMatchers("/home", "/register").permitAll(); //allow these url without authentication
            authorize.requestMatchers("/user/**").authenticated();
            authorize.requestMatchers("/api/**").authenticated();
            authorize.requestMatchers("/admin/**").authenticated();
            authorize.anyRequest().permitAll();
        }).oauth2Login(Customizer.withDefaults());

        //spring security default login
//        http.formLogin(Customizer.withDefaults());

//        customizing the login
        http.formLogin(form -> {
            form.loginPage("/login");
            form.loginProcessingUrl("/authenticate"); //login form will be submitted here
//            form.successForwardUrl("/user/dashboard"); //land here if login successful
//            form.failureForwardUrl("/login?error=true");
            form.defaultSuccessUrl(AppConstants.POST_LOGIN_REDIRECT_URL, true);
            form.failureUrl("/login?error=true");
            form.usernameParameter("email");
            form.passwordParameter("password");
            form.failureHandler(authFailureHandler);
                    /*
                    .failureHandler(new AuthenticationFailureHandler() { //this runs when authentication fails
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

                        }
                    })
                    .successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                        }
                    })*/
        });

        //OAuth config
        http.oauth2Login(oauth -> {
            oauth
                    .loginPage("/login")
                    .successHandler(oAuthAuthenticationSuccessHandler);
        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.logout(logOutForm -> {
            logOutForm.logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true");
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
