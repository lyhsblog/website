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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * WebFlux测试视图
 * 提供UI界面来测试WebFlux功能
 */
@Route(value = "webflux-test", layout = MainLayout.class)
@PageTitle("WebFlux Test")
@Menu(order = 6, icon = "vaadin:code", title = "WebFluxClient")
@PermitAll
public class WebFluxTestView extends VerticalLayout {

    private final WebFluxService webFluxService;
    private final WebClient webClient;
    private final TextArea resultArea;
    private final Div streamArea;

    public WebFluxTestView(WebFluxService webFluxService) {
        this.webFluxService = webFluxService;
        this.webClient = WebClient.create("http://localhost:8080");
        this.resultArea = new TextArea("结果");
        this.streamArea = new Div();

        initView();
    }

    private void initView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("WebFlux 测试");
        add(title);

        // Hello端点测试
        Button helloButton = new Button("测试 Hello 端点", e -> testHelloEndpoint());
        add(helloButton);

        // 流式数据测试
        Button streamButton = new Button("测试流式数据", e -> testStreamData());
        add(streamButton);

        // 处理数据测试
        Button processButton = new Button("测试数据处理", e -> testProcessData());
        add(processButton);

        // 获取任务测试
        Button tasksButton = new Button("获取任务列表", e -> testGetTasks());
        add(tasksButton);

        // 实时数据流测试
        Button realtimeButton = new Button("实时数据流", e -> testRealtimeStream());
        add(realtimeButton);

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

    private void testHelloEndpoint() {
        webClient.get()
                .uri("/api/webflux/hello")
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                        result -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("Hello 端点响应: " + result);
                                Notification.show("Hello 端点测试成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("错误: " + error.getMessage());
                                Notification.show("Hello 端点测试失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    private void testStreamData() {
        streamArea.removeAll();
        Paragraph header = new Paragraph("流式数据接收中...");
        streamArea.add(header);

        webClient.get()
                .uri("/api/webflux/stream")
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe(
                        data -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                Paragraph p = new Paragraph("收到: " + data);
                                streamArea.add(p);
                                streamArea.scrollIntoView();
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("流式数据错误: " + error.getMessage());
                                Notification.show("流式数据测试失败", 3000, Notification.Position.MIDDLE);
                            }));
                        },
                        () -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                Notification.show("流式数据接收完成");
                            }));
                        }
                );
    }

    private void testProcessData() {
        Map<String, Object> testData = Map.of("name", "测试数据", "value", 123);

        webClient.post()
                .uri("/api/webflux/process")
                .bodyValue(testData)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                        result -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("数据处理响应: " + result);
                                Notification.show("数据处理测试成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("数据处理错误: " + error.getMessage());
                                Notification.show("数据处理测试失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    private void testGetTasks() {
        webClient.get()
                .uri("/api/webflux/tasks")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                        tasks -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("任务列表: " + tasks);
                                Notification.show("获取任务列表成功");
                            }));
                        },
                        error -> {
                            getUI().ifPresent(ui -> ui.access(() -> {
                                resultArea.setValue("获取任务错误: " + error.getMessage());
                                Notification.show("获取任务列表失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }

    private void testRealtimeStream() {
        streamArea.removeAll();
        Paragraph header = new Paragraph("实时数据流接收中...");
        streamArea.add(header);

        webFluxService.generateDataStream()
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
                                Notification.show("实时数据流测试失败", 3000, Notification.Position.MIDDLE);
                            }));
                        }
                );
    }
}
