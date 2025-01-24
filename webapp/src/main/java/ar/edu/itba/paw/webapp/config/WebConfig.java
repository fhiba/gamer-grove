package ar.edu.itba.paw.webapp.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@EnableTransactionManagement
@ComponentScan({ "ar.edu.itba.paw.webapp.controller", "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistance",
        "ar.edu.itba.paw.webapp.mapper" })
// @EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private final static Long MAX_FILE_SIZE = (long) 5 * 1000 * 1000;
    private static final String DB_URL_PARAMETER = "DB_URL";
    private static final String DB_USERNAME_PARAMETER = "DB_USERNAME";
    private static final String DB_PASSWORD_PARAMETER = "DB_PASSWORD";

    private static final String PROD_DB_URL_PARAMETER = "PROD_DB_URL";
    private static final String PROD_DB_USERNAME_PARAMETER = "PROD_DB_USERNAME";
    private static final String PROD_DB_PASSWORD_PARAMETER = "PROD_DB_PASSWORD";

    //
    // @Bean
    // public ViewResolver viewResolver() {
    // final InternalResourceViewResolver viewResolver = new
    // InternalResourceViewResolver();
    // viewResolver.setViewClass(JstlView.class);
    // viewResolver.setPrefix("/WEB-INF/jsp/");
    // viewResolver.setSuffix(".jsp");
    // return viewResolver;
    // }
    //
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // // Call the super method to ensure default behavior
    // super.addResourceHandlers(registry);
    // registry.addResourceHandler("/css/**")
    // .addResourceLocations("/css/");
    // registry.addResourceHandler("/js/**")
    // .addResourceLocations("/js/");
    // registry.addResourceHandler("/images/**")
    // .addResourceLocations("/images/");
    // }
    //
    //
    //
    // paw-2024a-09
    // Zg1uq0uQi
    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        final Dotenv env = Dotenv.load();
        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl(env.get(DB_URL_PARAMETER));
        dataSource.setUsername(env.get(DB_USERNAME_PARAMETER));
        dataSource.setPassword(env.get(DB_PASSWORD_PARAMETER));
        return dataSource;
    }

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        messageSource.setCacheSeconds((int) TimeUnit.MINUTES.toSeconds(5));
        return messageSource;
    }
    //
    // @Bean(name = "multipartResolver")
    // public CommonsMultipartResolver multipartResolver() {
    // CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    // multipartResolver.setMaxUploadSize(MAX_FILE_SIZE);
    // return multipartResolver;
    // }
    //
    // @Bean
    // public LocaleResolver localeResolver() {
    // return new ApplicationLocaleResolver();
    // }
    //
    // @Bean
    // public LocaleChangeInterceptor localeChangeInterceptor() {
    // LocaleChangeInterceptor localeChangeInterceptor = new
    // LocaleChangeInterceptor();
    // localeChangeInterceptor.setParamName("lang");
    // return localeChangeInterceptor;
    // }

    // @Bean
    // public CustomLocaleChangeInterceptor customLocaleChangeInterceptor() {
    // return new CustomLocaleChangeInterceptor();
    // }
    //
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(customLocaleChangeInterceptor());
    //
    // }
    //
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());
        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect",
                "org.hibernate.dialect.PostgreSQL94Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("format_sql", "false");
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // High cache time
        registry.addResourceHandler("/index.html").addResourceLocations("/index.html").setCachePeriod(31556926);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
