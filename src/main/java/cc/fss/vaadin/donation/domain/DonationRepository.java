package cc.fss.vaadin.donation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    /**
     * 查询所有已确认的捐款
     */
    List<Donation> findByStatusOrderByDonationDateDesc(Donation.DonationStatus status);

    /**
     * 查询指定状态范围内的捐款
     */
    List<Donation> findByStatusInOrderByDonationDateDesc(List<Donation.DonationStatus> statuses);

    /**
     * 计算总捐款金额
     */
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") Donation.DonationStatus status);

    /**
     * 计算所有已确认和已完成的捐款总额
     */
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status IN ('CONFIRMED', 'COMPLETED')")
    BigDecimal sumTotalConfirmedAmount();

    /**
     * 查询最近的捐款记录
     */
    List<Donation> findTop10ByStatusInOrderByDonationDateDesc(List<Donation.DonationStatus> statuses);

    /**
     * 查询匿名捐款
     */
    List<Donation> findByAnonymousTrueOrderByDonationDateDesc();

    /**
     * 查询非匿名捐款
     */
    List<Donation> findByAnonymousFalseOrderByDonationDateDesc();

    /**
     * 统计指定状态的捐款数量
     */
    long countByStatusIn(List<Donation.DonationStatus> statuses);
}