package com.drone.rental.config;

import com.drone.rental.security.AdminInterceptor;
import com.drone.rental.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Value("${file.upload-path:uploads/}")
    private String uploadPath;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 认证相关
                        "/auth/login",
                        "/auth/admin/login",
                        "/auth/register",
                        // Swagger相关
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/doc.html",
                        // 静态资源
                        "/static/**",
                        "/uploads/**",
                        "/favicon.ico",
                        // 错误页面
                        "/error",
                        // 公开接口 - 无人机列表（不需要登录也可浏览）
                        "/drone/list",
                        "/drone/detail/**",
                        "/drone/brands",
                        "/drone/types"
                );

        // 管理员权限拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 上传文件访问 - 使用项目根目录下的uploads文件夹
        String userDir = System.getProperty("user.dir");
        String absoluteUploadPath = userDir + "/" + uploadPath;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadPath);
    }
}
