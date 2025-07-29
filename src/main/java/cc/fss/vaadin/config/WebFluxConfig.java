package cc.fss.vaadin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import cc.fss.vaadin.webflux.handler.WebFluxHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * WebFlux配置类
 * 配置路由和处理器
 */
@Configuration
public class WebFluxConfig {

    /**
     * 配置WebFlux路由
     */
    @Bean
    public RouterFunction<ServerResponse> webFluxRoutes(WebFluxHandler webFluxHandler) {
        return RouterFunctions
                .route(GET("/api/webflux/hello"), webFluxHandler::hello)
                .andRoute(GET("/api/webflux/stream"), webFluxHandler::streamData)
                .andRoute(POST("/api/webflux/process"), webFluxHandler::processData)
                .andRoute(GET("/api/webflux/tasks"), webFluxHandler::getTasks);
    }
}
