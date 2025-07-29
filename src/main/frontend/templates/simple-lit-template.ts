import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';

@customElement('simple-lit-template')
export class SimpleLitTemplate extends LitElement {
  @property({ type: String }) title = '简单 LitTemplate 组件';

  @state() private count = 0;
  @state() private currentTime = '';

  static styles = css`
    :host {
      display: block;
      font-family: Arial, sans-serif;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 10px;
      margin: 20px;
    }

    .header {
      text-align: center;
      margin-bottom: 20px;
    }

    .content {
      background: rgba(255, 255, 255, 0.1);
      padding: 20px;
      border-radius: 8px;
      backdrop-filter: blur(10px);
    }

    .button {
      background: rgba(255, 255, 255, 0.2);
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
      padding: 10px 20px;
      border-radius: 5px;
      cursor: pointer;
      margin: 5px;
      transition: all 0.3s ease;
      font-size: 14px;
    }

    .button:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: translateY(-2px);
    }

    .counter {
      font-size: 2em;
      font-weight: bold;
      text-align: center;
      margin: 20px 0;
    }

    .time-display {
      text-align: center;
      margin-top: 20px;
      font-size: 1.1em;
    }

    .button-container {
      text-align: center;
    }
  `;

  constructor() {
    super();
    this.updateTime();
    // 每秒更新时间
    setInterval(() => this.updateTime(), 1000);
  }

  render() {
    return html`
      <div class="header">
        <h2>${this.title}</h2>
        <p>这是一个使用 Vaadin LitTemplate 创建的简单组件</p>
      </div>

      <div class="content">
        <div class="counter">${this.count}</div>

        <div class="button-container">
          <button class="button" @click=${this.increment}>
            增加
          </button>
          <button class="button" @click=${this.decrement}>
            减少
          </button>
          <button class="button" @click=${this.reset}>
            重置
          </button>
        </div>

        <div class="time-display">
          <p>当前时间: ${this.currentTime}</p>
        </div>
      </div>
    `;
  }

  private increment() {
    this.count++;
  }

  private decrement() {
    this.count--;
  }

  private reset() {
    this.count = 0;
  }

  private updateTime() {
    const now = new Date();
    this.currentTime = now.toLocaleString('zh-CN');
  }
}
