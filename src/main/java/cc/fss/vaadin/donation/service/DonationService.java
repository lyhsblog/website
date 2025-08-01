package cc.fss.vaadin.donation.service;

import cc.fss.vaadin.donation.domain.Donation;
import cc.fss.vaadin.donation.domain.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    /**
     * 创建新的捐款记录
     */
    public Donation createDonation(Donation donation) {
        donation.setDonationDate(Instant.now());
        donation.setStatus(Donation.DonationStatus.PENDING);
        return donationRepository.save(donation);
    }

    /**
     * 更新捐款状态
     */
    public Donation updateDonationStatus(Long donationId, Donation.DonationStatus status) {
        Optional<Donation> optionalDonation = donationRepository.findById(donationId);
        if (optionalDonation.isPresent()) {
            Donation donation = optionalDonation.get();
            donation.setStatus(status);
            return donationRepository.save(donation);
        }
        throw new IllegalArgumentException("捐款记录不存在: " + donationId);
    }

    /**
     * 获取捐款记录
     */
    @Transactional(readOnly = true)
    public Optional<Donation> getDonationById(Long donationId) {
        return donationRepository.findById(donationId);
    }

    /**
     * 获取所有捐款记录
     */
    @Transactional(readOnly = true)
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    /**
     * 获取已确认的捐款记录
     */
    @Transactional(readOnly = true)
    public List<Donation> getConfirmedDonations() {
        return donationRepository.findByStatusOrderByDonationDateDesc(Donation.DonationStatus.CONFIRMED);
    }

    /**
     * 获取最近的捐款记录
     */
    @Transactional(readOnly = true)
    public List<Donation> getRecentDonations() {
        return donationRepository.findTop10ByStatusInOrderByDonationDateDesc(
            List.of(Donation.DonationStatus.CONFIRMED, Donation.DonationStatus.COMPLETED)
        );
    }

    /**
     * 计算总捐款金额
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalDonationAmount() {
        return donationRepository.sumTotalConfirmedAmount();
    }

    /**
     * 计算指定状态的捐款金额
     */
    @Transactional(readOnly = true)
    public BigDecimal getDonationAmountByStatus(Donation.DonationStatus status) {
        return donationRepository.sumAmountByStatus(status);
    }

    /**
     * 获取捐款统计信息
     */
    @Transactional(readOnly = true)
    public DonationStatistics getDonationStatistics() {
        BigDecimal totalAmount = getTotalDonationAmount();
        long totalCount = donationRepository.countByStatusIn(
            List.of(Donation.DonationStatus.CONFIRMED, Donation.DonationStatus.COMPLETED)
        );
        
        return new DonationStatistics(totalAmount, totalCount);
    }

    /**
     * 删除捐款记录
     */
    public void deleteDonation(Long donationId) {
        donationRepository.deleteById(donationId);
    }

    /**
     * 捐款统计信息
     */
    public static class DonationStatistics {
        private final BigDecimal totalAmount;
        private final long totalCount;

        public DonationStatistics(BigDecimal totalAmount, long totalCount) {
            this.totalAmount = totalAmount;
            this.totalCount = totalCount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public long getTotalCount() {
            return totalCount;
        }
    }
}