package cc.fss.vaadin.ui.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

/**
 * 自定义 LitTemplate 组件 - 使用 Lit 库创建
 */
@Tag("custom-lit-template")
@JsModule("./templates/custom-lit-template.ts")
public class CustomLitTemplateComponent extends LitTemplate {

    public CustomLitTemplateComponent() {
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
