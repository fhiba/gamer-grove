package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.CustomAccessDeniedHandler;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
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
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler("/loginFailed");
        simpleUrlAuthenticationFailureHandler.setUseForward(true);
        return simpleUrlAuthenticationFailureHandler;
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/images/**","/js/**"); //Apago SpringSecurity para los assets publicos
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
                .antMatchers("/login","/register", "/auth/forgotCredentials", "/auth/resetPassword").anonymous()
                .antMatchers("/profile","/profile/**").authenticated()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/auth/resend-verification").authenticated()
                .antMatchers("/home").authenticated()
                .antMatchers("/community/{communityName}/new","/community/{communityName}/follow", "/post","/community/{communityName}/rate").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/post/{postId}/+").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/post/{postId}/up").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/post").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/comment").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/community/{communityName}").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/community/{communityName}/deleteRating").hasRole("VERIFIED")
                .antMatchers(HttpMethod.POST,"/profile").authenticated()
                .antMatchers(HttpMethod.POST,"/new-community").hasRole("ADMIN")
                .antMatchers("/community/{communityName}/info").access("@modderServiceImpl.canEditCommunityInfo(#communityName) or hasRole('ADMIN')")
                .antMatchers("/post/{postId}/delete","/comment/{postId}/delete").access("@modderServiceImpl.canRemovePostAlternative(#postId) or hasRole('ADMIN')")                .antMatchers("/new-community","/addMod").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
            .and().formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/home",	false)
                .loginPage("/login")
                .failureHandler(authenticationFailureHandler())
            .and().rememberMe()
                .rememberMeParameter("j_rememberme")
                .userDetailsService(userDetailsService)
                .key(FileCopyUtils.copyToString(new InputStreamReader(rememberMeKey.getInputStream())))
                .tokenValiditySeconds((int)	TimeUnit.DAYS.toSeconds(30))
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
            .and().csrf().disable();
    }
}
