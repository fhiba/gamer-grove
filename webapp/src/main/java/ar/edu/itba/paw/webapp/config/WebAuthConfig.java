package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;


@EnableWebSecurity
@Configuration
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Value("classpath:rememberMe.key")
    private Resource rememberMeKey;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().
                antMatchers("/resources/**"); //Apago SpringSecurity para los assets publicos
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/login")
            .and().authorizeRequests()
                .antMatchers("/login","/register").anonymous()
                .antMatchers("/community/{communityName}/new", "/post").authenticated()
                .antMatchers(HttpMethod.POST,"/post/{postId}/+").authenticated()
                .antMatchers(HttpMethod.POST,"/post").authenticated()
                .antMatchers(HttpMethod.POST,"/comment").authenticated()
                .antMatchers("/new-community").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
            .and().formLogin()
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .defaultSuccessUrl("/",	false)
                .loginPage("/login")
            .and().rememberMe()
                .rememberMeParameter("j_rememberme")
                .userDetailsService(userDetailsService)
                .key(FileCopyUtils.copyToString(new InputStreamReader(rememberMeKey.getInputStream())))
                .tokenValiditySeconds((int)	TimeUnit.DAYS.toSeconds(30))
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and().exceptionHandling()
                .accessDeniedPage("/403")
            .and().csrf().disable();
    }
}
