package cc.fss.vaadin.ui.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

/**
 * 简单 LitTemplate 组件 - 使用 Lit 库创建
 */
@Tag("simple-lit-template")
@JsModule("./templates/simple-lit-template.ts")
public class SimpleLitTemplateComponent extends LitTemplate {

    public SimpleLitTemplateComponent() {
        // LitTemplate 会自动处理组件的渲染
    }

    /**
     * 设置组件标题
     */
    public void setTitle(String title) {
        getElement().setProperty("title", title);
    }

    /**
     * 获取组件标题
     */
    public String getTitle() {
        return getElement().getProperty("title", "");
    }
}
