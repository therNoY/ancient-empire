package pers.mihao.ancient_empire.startup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置静态资源映射
 **/
@Configuration
@Component
public class WebMvcConfig implements WebMvcConfigurer {

    static final String ORIGINS[] = new String[]{"GET", "POST", "PUT", "OPTIONS", "DELETE"};

    /**
     * 配置远程访问
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowCredentials(true)
            .allowedMethods(ORIGINS)
            .allowedHeaders("*")
            .exposedHeaders("access-control-allow-headers",
                "access-control-allow-methods",
                "access-control-allow-origin",
                "access-control-max-age",
                "X-Frame-Options")
            .allowCredentials(false)
            .maxAge(3600);
    }


}