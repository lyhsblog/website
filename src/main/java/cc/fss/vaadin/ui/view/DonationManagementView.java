package cc.fss.vaadin.ui.view;

import cc.fss.vaadin.base.ui.component.ViewToolbar;
import cc.fss.vaadin.donation.domain.Donation;
import cc.fss.vaadin.donation.service.DonationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 募捐管理页面 - 管理员查看和管理捐款记录
 */
@Route("donation-management")
@PageTitle("募捐管理")
@Menu(order = 5, icon = "vaadin:settings", title = "募捐管理")
@PermitAll
public class DonationManagementView extends VerticalLayout {

    private final DonationService donationService;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CHINA);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Grid<Donation> donationGrid;
    private ListDataProvider<Donation> dataProvider;
    private TextField searchField;
    private ComboBox<Donation.DonationStatus> statusFilter;
    private H3 totalAmountLabel;
    private H4 totalCountLabel;

    @Autowired
    public DonationManagementView(DonationService donationService) {
        this.donationService = donationService;

        setSpacing(true);
        setPadding(true);
        addClassName(LumoUtility.MaxWidth.LARGE);

        // 添加工具栏
        add(new ViewToolbar("募捐管理"));

        // 创建统计信息
        add(createStatisticsSection());

        // 创建过滤和搜索
        add(createFilterSection());

        // 创建数据表格
        add(createDonationGrid());

        // 加载数据
        loadDonations();
    }

    private Component createStatisticsSection() {
        VerticalLayout statsSection = new VerticalLayout();
        statsSection.setSpacing(false);
        statsSection.setPadding(true);
        statsSection.addClassName(LumoUtility.Background.CONTRAST_5);
        statsSection.addClassName(LumoUtility.BorderRadius.MEDIUM);

        H2 statsTitle = new H2("📊 募捐统计");
        statsTitle.addClassName(LumoUtility.FontSize.XLARGE);
        statsTitle.addClassName(LumoUtility.FontWeight.BOLD);

        totalAmountLabel = new H3();
        totalAmountLabel.addClassName(LumoUtility.FontSize.XXXLARGE);
        totalAmountLabel.addClassName(LumoUtility.FontWeight.BOLD);
        totalAmountLabel.addClassName(LumoUtility.TextColor.PRIMARY);

        totalCountLabel = new H4();
        totalCountLabel.addClassName(LumoUtility.FontSize.LARGE);
        totalCountLabel.addClassName(LumoUtility.TextColor.SECONDARY);

        statsSection.add(statsTitle, totalAmountLabel, totalCountLabel);
        return statsSection;
    }

    private Component createFilterSection() {
        HorizontalLayout filterSection = new HorizontalLayout();
        filterSection.setSpacing(true);
        filterSection.setPadding(true);
        filterSection.addClassName(LumoUtility.Background.CONTRAST_5);
        filterSection.addClassName(LumoUtility.BorderRadius.MEDIUM);

        // 搜索框
        searchField = new TextField("搜索");
        searchField.setPlaceholder("搜索姓名、邮箱或留言...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> filterDonations());

        // 状态过滤
        statusFilter = new ComboBox<>("状态");
        statusFilter.setItems(Donation.DonationStatus.values());
        statusFilter.setItemLabelGenerator(Donation.DonationStatus::getDisplayName);
        statusFilter.setPlaceholder("选择状态");
        statusFilter.addValueChangeListener(e -> filterDonations());

        // 刷新按钮
        Button refreshButton = new Button("刷新");
        refreshButton.addClickListener(e -> loadDonations());

        filterSection.add(searchField, statusFilter, refreshButton);
        return filterSection;
    }

    private Component createDonationGrid() {
        donationGrid = new Grid<>(Donation.class);
        donationGrid.setColumns();
        donationGrid.addClassName(LumoUtility.Width.FULL);

        // 配置列
        donationGrid.addColumn(Donation::getId)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        donationGrid.addColumn(donation -> currencyFormatter.format(donation.getAmount()))
                .setHeader("金额")
                .setWidth("120px")
                .setFlexGrow(0);

        donationGrid.addColumn(donation -> {
            if (donation.isAnonymous()) {
                return "匿名用户";
            }
            return donation.getDonorName() != null ? donation.getDonorName() : "未提供姓名";
        }).setHeader("捐款人")
          .setWidth("150px")
          .setFlexGrow(0);

        donationGrid.addColumn(Donation::getDonorEmail)
                .setHeader("邮箱")
                .setWidth("200px")
                .setFlexGrow(0);

        donationGrid.addColumn(Donation::getMessage)
                .setHeader("留言")
                .setWidth("300px")
                .setFlexGrow(1);

        donationGrid.addColumn(donation -> 
                donation.getDonationDate().atZone(java.time.ZoneId.systemDefault())
                        .format(dateFormatter))
                .setHeader("捐款时间")
                .setWidth("180px")
                .setFlexGrow(0);

        donationGrid.addColumn(donation -> donation.getStatus().getDisplayName())
                .setHeader("状态")
                .setWidth("100px")
                .setFlexGrow(0);

        // 添加操作列
        donationGrid.addComponentColumn(donation -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            if (donation.getStatus() == Donation.DonationStatus.PENDING) {
                Button confirmButton = new Button("确认");
                confirmButton.addClassName(LumoUtility.Background.SUCCESS);
                confirmButton.addClassName(LumoUtility.TextColor.SUCCESS_CONTRAST);
                confirmButton.addClickListener(e -> updateDonationStatus(donation, Donation.DonationStatus.CONFIRMED));
                actions.add(confirmButton);
            }

            if (donation.getStatus() == Donation.DonationStatus.CONFIRMED) {
                Button completeButton = new Button("完成");
                completeButton.addClassName(LumoUtility.Background.PRIMARY);
                completeButton.addClassName(LumoUtility.TextColor.PRIMARY_CONTRAST);
                completeButton.addClickListener(e -> updateDonationStatus(donation, Donation.DonationStatus.COMPLETED));
                actions.add(completeButton);
            }

            if (donation.getStatus() != Donation.DonationStatus.CANCELLED) {
                Button cancelButton = new Button("取消");
                cancelButton.addClassName(LumoUtility.Background.ERROR);
                cancelButton.addClassName(LumoUtility.TextColor.ERROR_CONTRAST);
                cancelButton.addClickListener(e -> updateDonationStatus(donation, Donation.DonationStatus.CANCELLED));
                actions.add(cancelButton);
            }

            return actions;
        }).setHeader("操作")
          .setWidth("200px")
          .setFlexGrow(0);

        return donationGrid;
    }

    private void loadDonations() {
        try {
            List<Donation> donations = donationService.getAllDonations();
            dataProvider = DataProvider.ofCollection(donations);
            donationGrid.setDataProvider(dataProvider);

            // 更新统计信息
            updateStatistics(donations);

        } catch (Exception e) {
            Notification.show("加载捐款数据失败: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void filterDonations() {
        if (dataProvider == null) return;

        String searchTerm = searchField.getValue() != null ? searchField.getValue().toLowerCase() : "";
        Donation.DonationStatus selectedStatus = statusFilter.getValue();

        List<Donation> filteredDonations = dataProvider.getItems().stream()
                .filter(donation -> {
                    // 搜索过滤
                    boolean matchesSearch = searchTerm.isEmpty() ||
                            (donation.getDonorName() != null && donation.getDonorName().toLowerCase().contains(searchTerm)) ||
                            (donation.getDonorEmail() != null && donation.getDonorEmail().toLowerCase().contains(searchTerm)) ||
                            (donation.getMessage() != null && donation.getMessage().toLowerCase().contains(searchTerm));

                    // 状态过滤
                    boolean matchesStatus = selectedStatus == null || donation.getStatus() == selectedStatus;

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toList());

        dataProvider = DataProvider.ofCollection(filteredDonations);
        donationGrid.setDataProvider(dataProvider);
    }

    private void updateDonationStatus(Donation donation, Donation.DonationStatus newStatus) {
        try {
            donationService.updateDonationStatus(donation.getId(), newStatus);
            Notification.show("状态更新成功", 3000, Notification.Position.MIDDLE);
            loadDonations(); // 重新加载数据
        } catch (Exception e) {
            Notification.show("状态更新失败: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void updateStatistics(List<Donation> donations) {
        BigDecimal totalAmount = donations.stream()
                .filter(d -> d.getStatus() == Donation.DonationStatus.CONFIRMED || 
                           d.getStatus() == Donation.DonationStatus.COMPLETED)
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCount = donations.stream()
                .filter(d -> d.getStatus() == Donation.DonationStatus.CONFIRMED || 
                           d.getStatus() == Donation.DonationStatus.COMPLETED)
                .count();

        totalAmountLabel.setText("总金额: " + currencyFormatter.format(totalAmount));
        totalCountLabel.setText("捐款人数: " + totalCount + " 人");
    }
}