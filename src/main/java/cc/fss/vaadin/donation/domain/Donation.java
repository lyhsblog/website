package cc.fss.vaadin.donation.domain;

import cc.fss.vaadin.base.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "donation")
public class Donation extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "donation_id")
    private Long id;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull
    @Min(value = 1, message = "捐款金额必须大于0")
    private BigDecimal amount;

    @Column(name = "donor_name", length = 100)
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Nullable
    private String donorName;

    @Column(name = "donor_email", length = 255)
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    @Nullable
    private String donorEmail;

    @Column(name = "message", length = 500)
    @Size(max = 500, message = "留言长度不能超过500个字符")
    @Nullable
    private String message;

    @Column(name = "donation_date", nullable = false)
    @NotNull
    private Instant donationDate;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous = false;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.PENDING;

    public enum DonationStatus {
        PENDING("待处理"),
        CONFIRMED("已确认"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String displayName;

        DonationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public @Nullable String getDonorName() {
        return donorName;
    }

    public void setDonorName(@Nullable String donorName) {
        this.donorName = donorName;
    }

    public @Nullable String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(@Nullable String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public @Nullable String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public Instant getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(Instant donationDate) {
        this.donationDate = donationDate;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }
}