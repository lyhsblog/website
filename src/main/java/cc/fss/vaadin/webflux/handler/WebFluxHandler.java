package cc.fss.vaadin.webflux.handler;

import cc.fss.vaadin.taskmanagement.domain.Task;
import cc.fss.vaadin.taskmanagement.service.TaskService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import org.springframework.data.domain.Pageable;

/**
 * WebFlux处理器
 * 提供响应式API端点
 */
@Component
public class WebFluxHandler {

    private final TaskService taskService;

    public WebFluxHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 简单的Hello端点
     */
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok()
                .bodyValue(Map.of("message", "Hello from WebFlux!", "timestamp", System.currentTimeMillis()));
    }

    /**
     * 流式数据端点
     */
    public Mono<ServerResponse> streamData(ServerRequest request) {
        Flux<String> dataStream = Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Data " + i)
                .take(10);

        return ServerResponse.ok()
                .body(dataStream, String.class);
    }

    /**
     * 处理数据端点
     */
    public Mono<ServerResponse> processData(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(data -> {
                    // 模拟异步处理
                    return Mono.just(Map.of(
                            "processed", true,
                            "input", data,
                            "result", "Data processed successfully",
                            "timestamp", System.currentTimeMillis()
                    ));
                })
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }

    /**
     * 获取任务列表端点
     */
    public Mono<ServerResponse> getTasks(ServerRequest request) {
        return ServerResponse.ok()
                .body(Flux.fromIterable(taskService.list(Pageable.unpaged())), Task.class);
    }
}
