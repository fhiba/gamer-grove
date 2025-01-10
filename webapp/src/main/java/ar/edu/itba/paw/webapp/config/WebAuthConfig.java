package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import ar.edu.itba.paw.webapp.auth.*;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@EnableWebSecurity
@Configuration
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    private static final String ACCESS_CONTROL_CHECK_USER = "@accessControl.checkUser(request, #id)";
    private static final String ACCESS_CONTROL_USER_HAS_IMAGE = "@accessControl.userHasImage(request)";
    private static final String ACCESS_CONTROL_IMAGE_IS_USER_IMAGE = "@accessControl.imageIsUserImage(request, #id)";






    private static final String AND = " and ";
    private static final String HAS_ROLE_USER = "hasRole('ROLE_USER')";
    private static final String HAS_ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
    private static final String HAS_ROLE_VERIFIED = "hasRole('ROLE_VERIFIED')";
    private static final String NOT = "!";

    private static final String IS_AUTHENTICATED = "isAuthenticated()";
    @Autowired
    private PawUserDetailsService userDetailsService;

    @Value("classpath:rememberMe.key")
    private Resource rememberMeKey;

    @Autowired
    private BasicAuthFilter basicAuthFilter;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AccessControl accessControl;

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new UnauthorizedRequestHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
                webExpressionVoter(),
                new RoleVoter(),
                new AuthenticatedVoter());
        return new UnanimousBased(decisionVoters);
    }

    @Bean

    public WebExpressionVoter webExpressionVoter() {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(webSecurityExpressionHandler());
        return webExpressionVoter;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        // TODO: CHECK IF NEEDED
        // expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public JwtUtil jwtUtil(@Value("classpath:jwt.key") Resource jwtKeyResource) throws Exception {
        return new JwtUtil(rememberMeKey);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader(ALL);

        configuration.setExposedHeaders(Arrays.asList("Authorization", "Link", "Location", "ETag", "Total-Elements",
                "X-GamerGrove-AuthToken", "X-GamerGrove-RefreshToken", "WWW-Authenticate"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/images/**", "/js/**"); // Apago SpringSecurity para los assets publicos
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                // Set stateless sesh
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // Exception handling
                .and().exceptionHandling()
                // unauth
                .authenticationEntryPoint(new UnauthorizedRequestHandler())
                // forbidden
                .accessDeniedHandler(new ForbiddenRequestHandler())
                .and().headers().cacheControl().disable()

                // Set permissions on endpoints
                .and().authorizeRequests()
                .accessDecisionManager(accessDecisionManager())

                /*
                 * Users
                 */
                // create user
                .antMatchers(HttpMethod.POST, "/api/users")
                .anonymous()
                // GETs
                .antMatchers(HttpMethod.GET, "/api/users")
                .access(HAS_ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/api/users/{id}")
                .permitAll()
                // reset password
                .requestMatchers(HttpMethod.PATCH, "/api/users/{id}")
                .anonymous()

                // get token for password reset
                .antMatchers(HttpMethod.POST, "/api/users/reset-password-token")
                .anonymous()

                // resend verify email
                .requestMatchers(HttpMethod.POST, "/api/users/{id}/verification-token")
                .access(ACCESS_CONTROL_CHECK_USER + AND + HAS_ROLE_USER + AND + NOT
                        + HAS_ROLE_VERIFIED)

                // update profile picture
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}")
                .access(ACCESS_CONTROL_CHECK_USER + AND + HAS_ROLE_VERIFIED)

                // update locale
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/locale")
                .access(ACCESS_CONTROL_CHECK_USER + AND + HAS_ROLE_VERIFIED)

                //images
                .requestMatchers(HttpMethod.GET, "/api/images/{id}")
                .permitAll()

                //create image
                .requestMatchers(HttpMethod.POST, "/api/images")
                .permitAll()

                //update image
                .requestMatchers(HttpMethod.PUT, "/api/images/{id}")
                .access(ACCESS_CONTROL_USER_HAS_IMAGE + AND + HAS_ROLE_VERIFIED + AND + ACCESS_CONTROL_IMAGE_IS_USER_IMAGE)

                //get all comunities paginated
                .requestMatchers(HttpMethod.GET, "/api/communities")
                .permitAll()

                //get comunity by name
                .requestMatchers(HttpMethod.GET, "/api/communities/{communityName}")
                .permitAll()

                .antMatchers("/api/**")
                .permitAll()

                // Disable client-side cache handling
                .and().headers().cacheControl().disable()

                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Enable CORS and disable csrf rules
                .cors().and().csrf().disable();
    }
}
