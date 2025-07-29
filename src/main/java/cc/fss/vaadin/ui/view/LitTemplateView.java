package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.base.ui.component.ViewToolbar;
import cc.fss.vaadin.ui.component.CustomLitTemplateComponent;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

/**
 * LitTemplate 视图 - 使用 Vaadin LitTemplate API
 */
@Route("lit-template")
@PageTitle("LitTemplate 页面")
@Menu(order = 3, icon = "vaadin:file-text", title = "LitTemplate")
@PermitAll
public class LitTemplateView extends Main {

    public LitTemplateView() {
        addClassName(LumoUtility.Padding.MEDIUM);

        // 添加工具栏
        add(new ViewToolbar("LitTemplate 示例"));

        // 添加自定义 LitTemplate 组件
        CustomLitTemplateComponent litComponent = new CustomLitTemplateComponent();
        litComponent.setTitle("自定义 LitTemplate 页面");
        add(litComponent);
    }
}
