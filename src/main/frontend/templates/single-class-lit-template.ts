import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('single-class-lit-template')
export class SingleClassLitTemplate extends LitElement {
  @property({ type: String }) title = '单类 LitTemplate 示例页面';
  @property({ type: String }) description = '这是一个只使用一个Java类的LitTemplate示例';

  static styles = css`
    :host {
      display: block;
      font-family: Arial, sans-serif;
      max-width: 1000px;
      margin: 0 auto;
      padding: 20px;
    }

    .header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 30px;
      border-radius: 10px;
      margin-bottom: 30px;
      text-align: center;
    }

    .content-section {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      border-left: 4px solid #667eea;
    }

    .feature-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 20px;
      margin: 20px 0;
    }

    .feature-card {
      background: white;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      border-top: 3px solid #667eea;
    }

    .feature-card h3 {
      color: #667eea;
      margin-top: 0;
    }

    .code-example {
      background: #2d3748;
      color: #e2e8f0;
      padding: 15px;
      border-radius: 6px;
      font-family: 'Courier New', monospace;
      font-size: 14px;
      overflow-x: auto;
      margin: 10px 0;
    }

    .highlight {
      color: #fbb6ce;
    }

    .comment {
      color: #68d391;
    }

    .btn {
      background: #667eea;
      color: white;
      padding: 12px 24px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 16px;
      transition: background 0.3s ease;
      margin: 5px;
    }

    .btn:hover {
      background: #5a6fd8;
    }

    .btn-secondary {
      background: #6c757d;
    }

    .btn-secondary:hover {
      background: #5a6268;
    }

    .comparison-table {
      width: 100%;
      border-collapse: collapse;
      margin: 20px 0;
    }

    .comparison-table th,
    .comparison-table td {
      border: 1px solid #ddd;
      padding: 12px;
      text-align: left;
    }

    .comparison-table th {
      background: #667eea;
      color: white;
    }

    .comparison-table tr:nth-child(even) {
      background: #f8f9fa;
    }

    .advantage {
      color: #28a745;
      font-weight: bold;
    }

    .disadvantage {
      color: #dc3545;
      font-weight: bold;
    }
  `;

  render() {
    return html`
      <div class="header">
        <h1>${this.title}</h1>
        <p>${this.description}</p>
      </div>

      <div class="content-section">
        <h2>单类 LitTemplate 的优势</h2>
        <div class="feature-grid">
          <div class="feature-card">
            <h3>🚀 简化架构</h3>
            <p>只需要一个Java类就能实现完整的LitTemplate功能，减少了代码复杂度。</p>
          </div>
          <div class="feature-card">
            <h3>📦 减少文件</h3>
            <p>不需要额外的组件类，直接在路由类中继承LitTemplate。</p>
          </div>
          <div class="feature-card">
            <h3>🔧 易于维护</h3>
            <p>所有相关逻辑都在一个类中，便于理解和维护。</p>
          </div>
        </div>
      </div>

      <div class="content-section">
        <h2>代码对比</h2>
        <table class="comparison-table">
          <thead>
            <tr>
              <th>方式</th>
              <th>Java类数量</th>
              <th>复杂度</th>
              <th>适用场景</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>传统方式（两个类）</td>
              <td>2个</td>
              <td class="disadvantage">较高</td>
              <td>复杂组件，需要复用</td>
            </tr>
            <tr>
              <td>单类方式</td>
              <td>1个</td>
              <td class="advantage">较低</td>
              <td>简单页面，无需复用</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="content-section">
        <h2>实现示例</h2>
        <h3>单类方式代码：</h3>
        <div class="code-example">
          <span class="comment">// 路由类直接继承 LitTemplate</span><br>
          <span class="highlight">@Route</span>("single-class-lit-template")<br>
          <span class="highlight">@Tag</span>("single-class-lit-template")<br>
          <span class="highlight">@JsModule</span>("./templates/single-class-lit-template.ts")<br>
          <span class="highlight">public class</span> SingleClassLitTemplateView <span class="highlight">extends LitTemplate</span> {<br>
          &nbsp;&nbsp;<span class="comment">// 直接在这里实现所有功能</span><br>
          }
        </div>

        <h3>传统方式代码：</h3>
        <div class="code-example">
          <span class="comment">// 需要两个类</span><br>
          <span class="highlight">public class</span> LitTemplateView <span class="highlight">extends Main</span> {<br>
          &nbsp;&nbsp;<span class="comment">// 路由类</span><br>
          &nbsp;&nbsp;add(<span class="highlight">new</span> CustomLitTemplateComponent());<br>
          }<br><br>
          <span class="highlight">public class</span> CustomLitTemplateComponent <span class="highlight">extends LitTemplate</span> {<br>
          &nbsp;&nbsp;<span class="comment">// 组件类</span><br>
          }
        </div>
      </div>

      <div class="content-section">
        <h2>使用建议</h2>
        <ul>
          <li><strong>简单页面</strong>：使用单类方式，代码更简洁</li>
          <li><strong>复杂组件</strong>：使用传统方式，便于复用和维护</li>
          <li><strong>团队开发</strong>：根据团队习惯和项目需求选择</li>
          <li><strong>性能考虑</strong>：两种方式性能差异不大</li>
        </ul>
      </div>

      <div style="text-align: center; margin-top: 30px;">
        <button class="btn" @click=${this.showInfo}>了解更多</button>
        <button class="btn btn-secondary" @click=${this.goBack}>返回</button>
      </div>
    `;
  }

  private showInfo() {
    alert('这是一个展示单类LitTemplate实现方式的示例页面！');
  }

  private goBack() {
    // 可以在这里添加导航逻辑
    console.log('返回上一页');
  }
}
