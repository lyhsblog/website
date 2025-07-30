package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.ui.component.SimpleLitTemplateComponent;
import cc.fss.vaadin.ui.layout.EmptyLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

/**
 * 独立 LitTemplate 视图 - 不被 Vaadin 布局包裹的独立页面
 */
@Route(value = "independent-lit-template", layout = EmptyLayout.class)
@PageTitle("独立 LitTemplate 视图")
@PermitAll
public class IndependentLitTemplateView extends Div {

    public IndependentLitTemplateView() {
        setSizeFull();
        addClassName(LumoUtility.Background.CONTRAST_10);
        // 内边距
        // addClassName(LumoUtility.Padding.LARGE);

        // 创建主容器
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setSpacing(true);
        mainContainer.setPadding(true);
        mainContainer.addClassName(LumoUtility.Background.CONTRAST_5);
        mainContainer.addClassName(LumoUtility.BorderRadius.MEDIUM);

        // 页面标题
        H1 title = new H1("独立 LitTemplate 视图");
        title.addClassName(LumoUtility.TextColor.PRIMARY);
        title.addClassName(LumoUtility.FontSize.XXXLARGE);
        title.addClassName(LumoUtility.FontWeight.BOLD);
        title.addClassName(LumoUtility.TextAlignment.CENTER);

        // 页面描述
        Paragraph description = new Paragraph(
            "这是一个独立的页面，不被 Vaadin 的默认布局包裹。页面完全自由，可以自定义任何布局。"
        );
        description.addClassName(LumoUtility.TextColor.SECONDARY);
        description.addClassName(LumoUtility.FontSize.LARGE);
        description.addClassName(LumoUtility.TextAlignment.CENTER);

        // 添加 LitTemplate 组件
        SimpleLitTemplateComponent litComponent = new SimpleLitTemplateComponent();
        litComponent.setTitle("独立 LitTemplate 组件");

        // 返回主页按钮
        Button backButton = new Button("返回主页", event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        backButton.addClassName(LumoUtility.Margin.MEDIUM);

        // 组装页面
        mainContainer.add(
            title,
            description,
            litComponent,
            backButton
        );

        add(mainContainer);
    }
}
