package cc.fss.vaadin.webflux.service;

import cc.fss.vaadin.taskmanagement.domain.Task;
import cc.fss.vaadin.taskmanagement.service.TaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * WebFlux服务类
 * 提供响应式业务逻辑
 */
@Service
public class WebFluxService {

    private final TaskService taskService;

    public WebFluxService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 异步获取任务列表
     */
    public Flux<Task> getTasksAsync() {
        return Mono.fromCallable(() -> taskService.list(Pageable.unpaged()))
                .flatMapMany(Flux::fromIterable);
    }

    /**
     * 异步处理任务
     */
    public Mono<Task> processTaskAsync(Task task) {
        return Mono.fromCallable(() -> {
            // 模拟异步处理
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 由于TaskService没有save方法，我们返回原任务
            return task;
        });
    }

    /**
     * 生成实时数据流
     */
    public Flux<Map<String, Object>> generateDataStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> Map.of(
                        "id", i,
                        "data", "Generated data " + i,
                        "timestamp", System.currentTimeMillis()
                ));
    }

    /**
     * 异步处理复杂数据
     */
    public Mono<Map<String, Object>> processComplexData(Map<String, Object> data) {
        return Mono.fromCallable(() -> {
            // 模拟复杂的异步处理
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return Map.of(
                    "processed", true,
                    "input", data,
                    "result", "Complex data processed successfully",
                    "timestamp", System.currentTimeMillis()
            );
        });
    }
}
