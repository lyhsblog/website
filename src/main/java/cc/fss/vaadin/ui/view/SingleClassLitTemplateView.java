package cc.fss.vaadin.ui.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 单类 LitTemplate 视图示例 - 直接继承 LitTemplate
 * 展示如何只使用一个类来实现 LitTemplate 功能
 */
@Route("single-class-lit-template")
@PageTitle("单类 LitTemplate 示例")
@Menu(order = 4, icon = "vaadin:code", title = "单类 LitTemplate")
@PermitAll
@Tag("single-class-lit-template")
@JsModule("./templates/single-class-lit-template.ts")
public class SingleClassLitTemplateView extends LitTemplate {

    public SingleClassLitTemplateView() {
        // 设置默认标题
        setTitle("单类 LitTemplate 示例页面");
        setDescription("这是一个只使用一个Java类的LitTemplate示例");
    }

    /**
     * 设置页面标题
     */
    public void setTitle(String title) {
        getElement().setProperty("title", title);
    }

    /**
     * 获取页面标题
     */
    public String getTitle() {
        return getElement().getProperty("title", "");
    }

    /**
     * 设置页面描述
     */
    public void setDescription(String description) {
        getElement().setProperty("description", description);
    }

    /**
     * 获取页面描述
     */
    public String getDescription() {
        return getElement().getProperty("description", "");
    }
}
