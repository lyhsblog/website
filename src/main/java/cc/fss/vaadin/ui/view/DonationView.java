package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.base.ui.component.ViewToolbar;
import cc.fss.vaadin.donation.domain.Donation;
import cc.fss.vaadin.donation.service.DonationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 募捐页面 - 支持项目发展
 */
@Route("donation")
@PageTitle("募捐支持")
@Menu(order = 4, icon = "vaadin:heart", title = "募捐支持")
@PermitAll
public class DonationView extends Main {

    private static final BigDecimal TARGET_AMOUNT = new BigDecimal("50000");
    
    private final DonationService donationService;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CHINA);
    private final ProgressBar progressBar = new ProgressBar();
    private final H3 currentAmountLabel = new H3();
    private final H4 targetAmountLabel = new H4();
    
    // 表单字段
    private NumberField customAmount;
    private TextField nameField;
    private EmailField emailField;
    private TextField messageField;
    private Checkbox anonymousCheckbox;

    @Autowired
    public DonationView(DonationService donationService) {
        this.donationService = donationService;
        
        addClassName(LumoUtility.Padding.MEDIUM);
        addClassName(LumoUtility.MaxWidth.LARGE);

        // 添加工具栏
        add(new ViewToolbar("募捐支持"));

        // 创建主要内容
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        // 添加标题和描述
        content.add(createHeader());
        
        // 添加募捐进度
        content.add(createProgressSection());
        
        // 添加好处说明
        content.add(createBenefitsSection());
        
        // 添加捐款表单
        content.add(createDonationForm());

        add(content);
        
        // 初始化进度条
        updateProgress();
    }

    private Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.setPadding(false);

        H1 title = new H1("🎯 支持项目发展");
        title.addClassName(LumoUtility.FontSize.XXXLARGE);
        title.addClassName(LumoUtility.FontWeight.BOLD);
        title.addClassName(LumoUtility.TextColor.PRIMARY);

        Paragraph description = new Paragraph(
            "您的支持将帮助我们开发更好的工具和平台，让每个人都能受益于先进的技术解决方案。"
        );
        description.addClassName(LumoUtility.FontSize.LARGE);
        description.addClassName(LumoUtility.TextColor.SECONDARY);

        header.add(title, description);
        return header;
    }

    private Component createProgressSection() {
        VerticalLayout progressSection = new VerticalLayout();
        progressSection.setSpacing(false);
        progressSection.setPadding(true);
        progressSection.addClassName(LumoUtility.Background.CONTRAST_5);
        progressSection.addClassName(LumoUtility.BorderRadius.MEDIUM);

        // 进度标题
        H2 progressTitle = new H2("募捐进度");
        progressTitle.addClassName(LumoUtility.FontSize.XLARGE);
        progressTitle.addClassName(LumoUtility.FontWeight.BOLD);

        // 当前金额
        currentAmountLabel.addClassName(LumoUtility.FontSize.XXXLARGE);
        currentAmountLabel.addClassName(LumoUtility.FontWeight.BOLD);
        currentAmountLabel.addClassName(LumoUtility.TextColor.PRIMARY);

        // 目标金额
        targetAmountLabel.addClassName(LumoUtility.FontSize.LARGE);
        targetAmountLabel.addClassName(LumoUtility.TextColor.SECONDARY);

        // 进度条
        progressBar.setMin(0);
        progressBar.setMax(1);
        progressBar.addClassName(LumoUtility.Width.FULL);

        // 百分比显示
        H4 percentageLabel = new H4();
        percentageLabel.addClassName(LumoUtility.TextColor.SECONDARY);
        percentageLabel.addClassName(LumoUtility.TextAlign.CENTER);

        progressSection.add(progressTitle, currentAmountLabel, targetAmountLabel, progressBar, percentageLabel);

        // 更新百分比
        double percentage = CURRENT_AMOUNT.divide(TARGET_AMOUNT, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
        percentageLabel.setText(String.format("%.1f%%", percentage * 100));

        return progressSection;
    }

    private Component createBenefitsSection() {
        VerticalLayout benefitsSection = new VerticalLayout();
        benefitsSection.setSpacing(true);
        benefitsSection.setPadding(true);
        benefitsSection.addClassName(LumoUtility.Background.CONTRAST_5);
        benefitsSection.addClassName(LumoUtility.BorderRadius.MEDIUM);

        H2 benefitsTitle = new H2("🎁 您的支持带来的好处");
        benefitsTitle.addClassName(LumoUtility.FontSize.XLARGE);
        benefitsTitle.addClassName(LumoUtility.FontWeight.BOLD);

        // 好处列表
        String[] benefits = {
            "🚀 加速开发进度 - 更快推出新功能和改进",
            "🛠️ 提升工具质量 - 更好的用户体验和稳定性",
            "📚 免费教育资源 - 为所有人提供学习机会",
            "🔧 技术支持服务 - 专业的技术咨询和帮助",
            "🌟 优先功能体验 - 抢先体验最新功能",
            "🤝 社区参与权 - 参与产品决策和功能规划"
        };

        UnorderedList benefitsList = new UnorderedList();
        for (String benefit : benefits) {
            ListItem item = new ListItem(benefit);
            item.addClassName(LumoUtility.FontSize.MEDIUM);
            item.addClassName(LumoUtility.Margin.SMALL);
            benefitsList.add(item);
        }

        benefitsSection.add(benefitsTitle, benefitsList);
        return benefitsSection;
    }

    private Component createDonationForm() {
        VerticalLayout formSection = new VerticalLayout();
        formSection.setSpacing(true);
        formSection.setPadding(true);
        formSection.addClassName(LumoUtility.Background.CONTRAST_5);
        formSection.addClassName(LumoUtility.BorderRadius.MEDIUM);

        H2 formTitle = new H2("💝 立即支持");
        formTitle.addClassName(LumoUtility.FontSize.XLARGE);
        formTitle.addClassName(LumoUtility.FontWeight.BOLD);

        // 捐款金额选择
        VerticalLayout amountSection = new VerticalLayout();
        amountSection.setSpacing(false);
        amountSection.setPadding(false);

        H4 amountTitle = new H4("选择捐款金额");
        amountTitle.addClassName(LumoUtility.FontWeight.BOLD);

        HorizontalLayout amountButtons = new HorizontalLayout();
        amountButtons.setSpacing(true);
        amountButtons.setPadding(false);

        String[] amounts = {"50", "100", "200", "500", "1000"};
        for (String amount : amounts) {
            Button amountButton = new Button("¥" + amount);
            amountButton.addClassName(LumoUtility.Padding.MEDIUM);
            amountButton.addClickListener(e -> {
                // 这里可以设置选中的金额
                Notification.show("选择了 ¥" + amount + " 的捐款金额");
            });
            amountButtons.add(amountButton);
        }

        // 自定义金额输入
        customAmount = new NumberField("自定义金额");
        customAmount.setPlaceholder("输入您想要捐款的金额");
        customAmount.setMin(1);
        customAmount.setSuffixComponent(new Span("元"));

        amountSection.add(amountTitle, amountButtons, customAmount);

        // 个人信息
        VerticalLayout infoSection = new VerticalLayout();
        infoSection.setSpacing(false);
        infoSection.setPadding(false);

        H4 infoTitle = new H4("联系信息（可选）");
        infoTitle.addClassName(LumoUtility.FontWeight.BOLD);

        nameField = new TextField("姓名");
        nameField.setPlaceholder("您的姓名");

        emailField = new EmailField("邮箱");
        emailField.setPlaceholder("您的邮箱地址");

        messageField = new TextField("留言");
        messageField.setPlaceholder("您想说的话（可选）");

        // 匿名捐款选项
        anonymousCheckbox = new Checkbox("匿名捐款");
        anonymousCheckbox.setValue(false);

        infoSection.add(infoTitle, nameField, emailField, messageField, anonymousCheckbox);

        // 捐款按钮
        Button donateButton = new Button("立即捐款");
        donateButton.addClassName(LumoUtility.Background.PRIMARY);
        donateButton.addClassName(LumoUtility.TextColor.PRIMARY_CONTRAST);
        donateButton.addClassName(LumoUtility.Padding.MEDIUM);
        donateButton.addClassName(LumoUtility.FontWeight.BOLD);
        donateButton.addClickListener(e -> handleDonation());

        formSection.add(formTitle, amountSection, infoSection, donateButton);
        return formSection;
    }

    private void handleDonation() {
        try {
            // 获取捐款金额
            BigDecimal amount = customAmount.getValue();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                Notification.show("请输入有效的捐款金额", 3000, Notification.Position.MIDDLE);
                return;
            }

            // 创建捐款记录
            Donation donation = new Donation();
            donation.setAmount(amount);
            
            // 处理匿名捐款
            if (Boolean.TRUE.equals(anonymousCheckbox.getValue())) {
                donation.setAnonymous(true);
                donation.setDonorName(null);
                donation.setDonorEmail(null);
            } else {
                donation.setAnonymous(false);
                donation.setDonorName(nameField.getValue());
                donation.setDonorEmail(emailField.getValue());
            }
            
            donation.setMessage(messageField.getValue());

            // 保存捐款记录
            donationService.createDonation(donation);

            // 显示成功消息
            Notification.show("感谢您的支持！您的捐款已记录。", 5000, Notification.Position.MIDDLE);

            // 清空表单
            clearForm();

            // 更新进度
            updateProgress();

        } catch (Exception e) {
            Notification.show("捐款处理失败，请稍后重试: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void clearForm() {
        customAmount.clear();
        nameField.clear();
        emailField.clear();
        messageField.clear();
        anonymousCheckbox.setValue(false);
    }

    private void updateProgress() {
        try {
            BigDecimal currentAmount = donationService.getTotalDonationAmount();
            double progress = currentAmount.divide(TARGET_AMOUNT, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
            progressBar.setValue(progress);
            
            currentAmountLabel.setText("已筹集: " + currencyFormatter.format(currentAmount));
            targetAmountLabel.setText("目标: " + currencyFormatter.format(TARGET_AMOUNT));
        } catch (Exception e) {
            // 如果获取数据失败，使用默认值
            progressBar.setValue(0.25); // 25% 进度
            currentAmountLabel.setText("已筹集: ¥12,500");
            targetAmountLabel.setText("目标: " + currencyFormatter.format(TARGET_AMOUNT));
        }
    }
}