import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';

interface UserData {
  name: string;
  email: string;
  message: string;
  timestamp: Date;
}

@customElement('custom-lit-template')
export class CustomLitTemplate extends LitElement {
  @property({ type: String }) title = '自定义 LitTemplate 页面';

  @state() private submissions: UserData[] = [];
  @state() private formData = {
    name: '',
    email: '',
    message: ''
  };
  @state() private showStatus = false;
  @state() private statusMessage = '';
  @state() private statusType: 'success' | 'error' = 'success';

  static styles = css`
    :host {
      display: block;
      font-family: Arial, sans-serif;
      max-width: 800px;
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

    .form-group {
      margin-bottom: 15px;
    }

    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
      color: #333;
    }

    .form-group input, .form-group textarea {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
      box-sizing: border-box;
    }

    .form-group textarea {
      height: 100px;
      resize: vertical;
    }

    .form-group input:invalid, .form-group textarea:invalid {
      border-color: #dc3545;
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
      margin-right: 10px;
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

    .card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .status-message {
      padding: 10px;
      border-radius: 4px;
      margin: 10px 0;
      display: none;
    }

    .status-message.show {
      display: block;
    }

    .status-success {
      background: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }

    .status-error {
      background: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }

    .history-item {
      background: #f8f9fa;
      padding: 15px;
      margin-bottom: 10px;
      border-radius: 6px;
      border-left: 3px solid #667eea;
    }

    .history-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
    }

    .delete-btn {
      background: #dc3545;
      color: white;
      border: none;
      padding: 5px 10px;
      border-radius: 3px;
      cursor: pointer;
      font-size: 12px;
      margin-top: 10px;
    }

    .delete-btn:hover {
      background: #c82333;
    }

    .no-data {
      color: #666;
      font-style: italic;
      text-align: center;
      padding: 20px;
    }
  `;

  constructor() {
    super();
    this.loadFromLocalStorage();
    console.log('sldfjsdjfl')
    console.log('sldfjsdjfl')
  }

  render() {
    return html`
      <div class="header">
        <h1>${this.title}</h1>
        <p>这是一个使用 Vaadin LitTemplate 创建的自定义页面</p>
      </div>

      <div class="content-section">
        <h2>用户信息表单</h2>
        <form @submit=${this.handleFormSubmit}>
          <div class="form-group">
            <label for="name">姓名:</label>
            <input
              type="text"
              id="name"
              name="name"
              required
              .value=${this.formData.name}
              @input=${this.handleInputChange}
            >
          </div>

          <div class="form-group">
            <label for="email">邮箱:</label>
            <input
              type="email"
              id="email"
              name="email"
              required
              .value=${this.formData.email}
              @input=${this.handleInputChange}
            >
          </div>

          <div class="form-group">
            <label for="message">留言:</label>
            <textarea
              id="message"
              name="message"
              placeholder="请输入您的留言..."
              .value=${this.formData.message}
              @input=${this.handleInputChange}
            ></textarea>
          </div>
          <button type="submit" class="btn">提交</button>
          <button type="button" class="btn btn-secondary" @click=${this.clearForm}>清空</button>
        </form>
      </div>

      <div class="card">
        <h3>提交历史</h3>
        <div id="historyList">
          ${this.renderHistory()}
        </div>
      </div>

      <div class="status-message ${this.showStatus ? 'show' : ''} status-${this.statusType}">
        ${this.statusMessage}
      </div>
    `;
  }

  private handleInputChange(e: Event) {
    const target = e.target as HTMLInputElement | HTMLTextAreaElement;
    const { name, value } = target;

    this.formData = {
      ...this.formData,
      [name]: value
    };
  }

  private handleFormSubmit(e: Event) {
    e.preventDefault();

    if (this.validateForm()) {
      const userData: UserData = {
        ...this.formData,
        timestamp: new Date()
      };

      this.submissions.unshift(userData);
      this.saveToLocalStorage();
      this.showStatusMessage('表单提交成功！', 'success');
      this.clearForm();
    } else {
      this.showStatusMessage('请检查表单数据是否正确填写', 'error');
    }
  }

  private validateForm(): boolean {
    return this.formData.name.trim().length > 0 &&
           this.formData.email.trim().length > 0 &&
           this.isValidEmail(this.formData.email);
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private clearForm() {
    this.formData = {
      name: '',
      email: '',
      message: ''
    };
    this.hideStatusMessage();
  }

  private renderHistory() {
    if (this.submissions.length === 0) {
      return html`<div class="no-data">暂无提交记录</div>`;
    }

    return this.submissions.map((submission, index) => html`
      <div class="history-item">
        <div class="history-header">
          <strong>${submission.name}</strong>
          <small style="color: #666;">${this.formatDate(submission.timestamp)}</small>
        </div>
        <div style="margin-bottom: 5px;">
          <strong>邮箱:</strong> ${submission.email}
        </div>
        ${submission.message ? html`<div><strong>留言:</strong> ${submission.message}</div>` : ''}
        <button
          class="delete-btn"
          @click=${() => this.deleteSubmission(index)}
        >
          删除
        </button>
      </div>
    `);
  }

  private deleteSubmission(index: number) {
    this.submissions.splice(index, 1);
    this.saveToLocalStorage();
    this.showStatusMessage('记录已删除', 'success');
  }

  private formatDate(date: Date): string {
    return new Intl.DateTimeFormat('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }

  private showStatusMessage(message: string, type: 'success' | 'error') {
    this.statusMessage = message;
    this.statusType = type;
    this.showStatus = true;

    // 3秒后自动隐藏
    setTimeout(() => {
      this.hideStatusMessage();
    }, 3000);
  }

  private hideStatusMessage() {
    this.showStatus = false;
  }

  private saveToLocalStorage() {
    localStorage.setItem('customLitTemplateSubmissions', JSON.stringify(this.submissions));
  }

  private loadFromLocalStorage() {
    const saved = localStorage.getItem('customLitTemplateSubmissions');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        this.submissions = parsed.map((item: any) => ({
          ...item,
          timestamp: new Date(item.timestamp)
        }));
      } catch (e) {
        console.error('Failed to load submissions from localStorage:', e);
      }
    }
  }
}
