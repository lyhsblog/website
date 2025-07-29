# Vaadin LitTemplate 使用指南

本指南展示了如何在 Vaadin 项目中使用 LitTemplate 创建自定义页面和组件。LitTemplate 是 Vaadin 推荐的现代方式，基于 Lit 库，提供了更好的性能和开发体验。

## 概述

LitTemplate 是 Vaadin 的现代模板系统，基于 Google 的 Lit 库。它提供了：
- **声明式渲染**：使用模板字面量语法
- **响应式更新**：自动响应状态变化
- **样式封装**：使用 Shadow DOM 隔离样式
- **TypeScript 支持**：完整的类型安全
- **高性能**：基于 Web Components 标准

## 创建的文件

### 1. LitTemplate 文件
- `src/main/frontend/templates/custom-lit-template.ts` - 复杂的 LitTemplate 组件
- `src/main/frontend/templates/simple-lit-template.ts` - 简单的 LitTemplate 组件

### 2. Java 组件类
- `src/main/java/cc/fss/vaadin/ui/component/CustomLitTemplateComponent.java` - 复杂组件包装器
- `src/main/java/cc/fss/vaadin/ui/component/SimpleLitTemplateComponent.java` - 简单组件包装器

### 3. 视图类
- `src/main/java/cc/fss/vaadin/ui/view/LitTemplateView.java` - LitTemplate 页面视图
- `src/main/java/cc/fss/vaadin/ui/view/SimpleLitTemplateView.java` - 简单 LitTemplate 视图

### 4. 配置文件
- `package.json` - 前端依赖管理
- `tsconfig.json` - TypeScript 配置

## 使用方法

### 1. 创建 LitTemplate 组件

```typescript
// src/main/frontend/templates/your-lit-component.ts
import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';

@customElement('your-lit-component')
export class YourLitComponent extends LitElement {
  @property({ type: String }) title = '默认标题';
  @state() private count = 0;

  static styles = css`
    :host {
      display: block;
      padding: 20px;
    }

    .button {
      background: #667eea;
      color: white;
      padding: 10px 20px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
  `;

  render() {
    return html`
      <div>
        <h2>${this.title}</h2>
        <p>计数: ${this.count}</p>
        <button class="button" @click=${this.increment}>
          增加
        </button>
      </div>
    `;
  }

  private increment() {
    this.count++;
  }
}
```

### 2. 创建 Java 包装器

```java
// src/main/java/cc/fss/vaadin/ui/component/YourLitComponent.java
@Tag("your-lit-component")
@JsModule("./templates/your-lit-component.ts")
public class YourLitComponent extends LitTemplate {

    public YourLitComponent() {
        // LitTemplate 会自动处理组件的渲染
    }

    public void setTitle(String title) {
        getElement().setProperty("title", title);
    }

    public String getTitle() {
        return getElement().getProperty("title", "");
    }
}
```

### 3. 在视图中使用

```java
@Route("your-page")
public class YourView extends Main {
    public YourView() {
        YourLitComponent component = new YourLitComponent();
        component.setTitle("我的组件");
        add(component);
    }
}
```

## LitTemplate 核心概念

### 1. 装饰器

- `@customElement('tag-name')` - 注册自定义元素
- `@property()` - 定义组件的公共属性
- `@state()` - 定义组件的内部状态
- `@query()` - 查询组件内部的元素

### 2. 模板语法

```typescript
render() {
  return html`
    <div>
      <!-- 文本插值 -->
      <h1>${this.title}</h1>

      <!-- 条件渲染 -->
      ${this.showContent ? html`<p>内容</p>` : ''}

      <!-- 列表渲染 -->
      ${this.items.map(item => html`<div>${item}</div>`)}

      <!-- 事件处理 -->
      <button @click=${this.handleClick}>点击</button>

      <!-- 属性绑定 -->
      <input .value=${this.inputValue} @input=${this.handleInput}>
    </div>
  `;
}
```

### 3. 样式定义

```typescript
static styles = css`
  :host {
    display: block;
    font-family: Arial, sans-serif;
  }

  .container {
    padding: 20px;
    background: #f5f5f5;
  }

  .button {
    background: #667eea;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
  }

  .button:hover {
    background: #5a6fd8;
  }
`;
```

## 与 Vaadin 集成

### 1. 数据绑定

```typescript
// 在 LitTemplate 中
@property({ type: String }) value = '';

// 在 Java 中
component.setValue("新值");
String value = component.getValue();
```

### 2. 事件处理

```typescript
// 在 LitTemplate 中
private handleSubmit() {
  this.dispatchEvent(new CustomEvent('submit', {
    detail: { data: this.formData },
    bubbles: true,
    composed: true
  }));
}

// 在 Java 中
component.addListener("submit", event -> {
  // 处理提交事件
});
```

### 3. 样式集成

```typescript
// 使用 Vaadin 主题变量
static styles = css`
  :host {
    --vaadin-primary-color: #667eea;
    --vaadin-primary-contrast-color: white;
  }

  .button {
    background: var(--vaadin-primary-color);
    color: var(--vaadin-primary-contrast-color);
  }
`;
```

## 最佳实践

### 1. 组件设计

- **单一职责**：每个组件只负责一个功能
- **可重用性**：设计通用的、可配置的组件
- **封装性**：使用 Shadow DOM 隔离样式和行为

### 2. 性能优化

- **懒加载**：只在需要时加载组件
- **状态管理**：合理使用 `@state` 和 `@property`
- **事件委托**：减少事件监听器数量

### 3. 类型安全

- **TypeScript**：使用 TypeScript 获得完整的类型检查
- **接口定义**：为组件属性定义清晰的接口
- **类型断言**：谨慎使用类型断言

### 4. 测试

```typescript
// 组件测试示例
import { fixture, expect } from '@open-wc/testing';
import { YourLitComponent } from './your-lit-component.js';

describe('YourLitComponent', () => {
  it('renders with default title', async () => {
    const el = await fixture<YourLitComponent>(html`<your-lit-component></your-lit-component>`);
    expect(el.shadowRoot!.querySelector('h2')!.textContent).to.equal('默认标题');
  });
});
```

## 访问页面

启动应用程序后，您可以访问以下页面：

- `/lit-template` - LitTemplate 页面（复杂表单）
- `/simple-lit-template` - 简单 LitTemplate 示例（计数器）

## 开发工作流

### 1. 开发模式

```bash
# 启动开发服务器
./mvnw

# 前端资源会自动重新加载
```

### 2. 生产构建

```bash
# 构建生产版本
./mvnw -Pproduction package
```

### 3. 调试

- 使用浏览器开发者工具查看 Shadow DOM
- 使用 Lit DevTools 扩展（如果可用）
- 在 TypeScript 文件中设置断点

## 注意事项

1. **浏览器支持**：LitTemplate 需要现代浏览器支持
2. **构建工具**：确保 Vaadin Maven 插件正确配置
3. **依赖管理**：使用 package.json 管理前端依赖
4. **样式隔离**：注意 Shadow DOM 的样式封装特性

## 扩展阅读

- [Lit 官方文档](https://lit.dev/)
- [Vaadin LitTemplate 文档](https://vaadin.com/docs/latest/flow/templates/lit-template)
- [Web Components 规范](https://developer.mozilla.org/en-US/docs/Web/Web_Components)
- [TypeScript 装饰器](https://www.typescriptlang.org/docs/handbook/decorators.html)
