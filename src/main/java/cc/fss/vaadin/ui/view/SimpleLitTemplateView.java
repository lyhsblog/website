package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.base.ui.component.ViewToolbar;
import cc.fss.vaadin.ui.component.SimpleLitTemplateComponent;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

/**
 * 简单 LitTemplate 视图 - 展示基本的 LitTemplate 使用
 */
@Route("simple-lit-template")
@PageTitle("简单 LitTemplate")
@Menu(order = 4, icon = "vaadin:code", title = "简单 LitTemplate")
@PermitAll
public class SimpleLitTemplateView extends Main {

    public SimpleLitTemplateView() {
        addClassName(LumoUtility.Padding.MEDIUM);

        // 添加工具栏
        add(new ViewToolbar("简单 LitTemplate 示例"));

        // 添加简单 LitTemplate 组件
        SimpleLitTemplateComponent litComponent = new SimpleLitTemplateComponent();
        litComponent.setTitle("简单 LitTemplate 组件");
        add(litComponent);
    }
}
