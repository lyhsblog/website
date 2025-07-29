package cc.fss.vaadin.webflux.ui.view;

import cc.fss.vaadin.base.ui.view.MainLayout;
import cc.fss.vaadin.webflux.service.WebFluxService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.PermitAll;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * WebFlux服务端内部使用示例
 * 展示如何在服务端直接使用WebFlux，而不通过API端点
 */
@Route(value = "webflux-service", layout = MainLayout.class)
@PageTitle("WebFlux Service")
@Menu(order = 5, icon = "vaadin:code", title = "WebFluxService")
@PermitAll
public class WebFluxServiceView extends VerticalLayout {

    private final WebFluxService webFluxService;
    private final TextArea resultArea;
    private final Div streamArea;

    public WebFluxServiceView(WebFluxService webFluxService) {
        this.webFluxService = webFluxService;
        this.resultArea = new TextArea("处理结果");
        this.streamArea = new Div();

        initView();
    }

    private void initView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("WebFlux 服务端内部使用示例");
        add(title);

        // 异步任务处理
        Button asyncTaskButton = new Button("异步处理任务", e -> testAsyncTaskProcessing());
        add(asyncTaskButton);

        // 实时数据流
        Button realtimeStreamButton = new Button("实时数据流", e -> testRealtimeStream());
        add(realtimeStreamButton);

        // 复杂数据处理
        Button complexDataButton = new Button("复杂数据处理", e -> testComplexDataProcessing());
        add(complexDataButton);

        // 任务列表异步获取
        Button tasksButton = new Button("异步获取任务列表", e -> testAsyncTaskList());
        add(tasksButton);

        // 并发处理
        Button concurrentButton = new Button("并发处理", e -> testConcurrentProcessing());
        add(concurrentButton);

        // 结果显示区域
        resultArea.setWidth("100%");
        resultArea.setHeight("200px");
        add(resultArea);

        // 流式结果显示区域
        streamArea.setWidth("100%");
        streamArea.setHeight("300px");
        streamArea.getStyle().set("border", "1px solid #ccc");
        streamArea.getStyle().set("padding", "10px");
        streamArea.getStyle().set("overflow-y", "auto");
        add(streamArea);
    }

    /**
     * 测试异步任务处理
     */
    private void testAsyncTaskProcessing() {
        resultArea.setValue("开始异步任务处理...");

        // 模拟创建任务
        var task = new cc.fss.vaadin.taskmanagement.domain.Task();
        task.setDescription("异步处理的任务");

        webFluxService.processTaskAsync(task)
                .subscribe(
                        processedTask -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("任务处理完成: " + processedTask.getDescription());
                                Notification.show("异步任务处理成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("任务处理失败: " + error.getMessage());
                                Notification.show("异步任务处理失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    /**
     * 测试实时数据流
     */
    private void testRealtimeStream() {
        streamArea.removeAll();
        Paragraph header = new Paragraph("实时数据流处理中...");
        streamArea.add(header);

        webFluxService.generateDataStream()
                .take(10) // 只取前10个数据
                .subscribe(
                        data -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                Paragraph p = new Paragraph("实时数据: " + data);
                                streamArea.add(p);
                                streamArea.scrollIntoView();
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("实时数据流错误: " + error.getMessage());
                                Notification.show("实时数据流处理失败", 3000, Notification.Position.MIDDLE);
                            }));
                        },
                        () -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                Notification.show("实时数据流处理完成");
                            }));
                        }
                );
    }

    /**
     * 测试复杂数据处理
     */
    private void testComplexDataProcessing() {
        resultArea.setValue("开始复杂数据处理...");

        Map<String, Object> complexData = Map.of(
                "name", "复杂数据",
                "value", 123,
                "items", new String[]{"item1", "item2", "item3"},
                "metadata", Map.of("type", "test", "version", "1.0")
        );

        webFluxService.processComplexData(complexData)
                .subscribe(
                        result -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("复杂数据处理完成: " + result);
                                Notification.show("复杂数据处理成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("复杂数据处理失败: " + error.getMessage());
                                Notification.show("复杂数据处理失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    /**
     * 测试异步获取任务列表
     */
    private void testAsyncTaskList() {
        resultArea.setValue("异步获取任务列表...");

        webFluxService.getTasksAsync()
                .collectList()
                .subscribe(
                        tasks -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("任务列表获取完成，共 " + tasks.size() + " 个任务");
                                Notification.show("异步获取任务列表成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("获取任务列表失败: " + error.getMessage());
                                Notification.show("异步获取任务列表失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    /**
     * 测试并发处理
     */
    private void testConcurrentProcessing() {
        resultArea.setValue("开始并发处理...");

        // 创建多个并发任务
        Mono<String> task1 = Mono.fromCallable(() -> {
            Thread.sleep(1000);
            return "任务1完成";
        });

        Mono<String> task2 = Mono.fromCallable(() -> {
            Thread.sleep(800);
            return "任务2完成";
        });

        Mono<String> task3 = Mono.fromCallable(() -> {
            Thread.sleep(1200);
            return "任务3完成";
        });

        // 并发执行所有任务
        Flux.zip(task1, task2, task3)
                .subscribe(
                        results -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("并发处理完成:\n" +
                                        "结果1: " + results.getT1() + "\n" +
                                        "结果2: " + results.getT2() + "\n" +
                                        "结果3: " + results.getT3());
                                Notification.show("并发处理成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("并发处理失败: " + error.getMessage());
                                Notification.show("并发处理失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }
}
