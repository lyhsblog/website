package cc.fss.vaadin.ui.layout;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;

/**
 * 空布局 - 不提供任何布局元素的布局类
 * 用于创建完全独立的页面，不被 Vaadin 默认布局包裹
 */
public class EmptyLayout extends Div implements RouterLayout {

    public EmptyLayout() {
        setSizeFull();
    }
}
