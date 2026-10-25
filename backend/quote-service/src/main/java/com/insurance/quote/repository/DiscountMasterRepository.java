
package com.insurance.quote.repository;


import com.insurance.quote.entity.DiscountMaster;
import com.insurance.quote.enums.DiscountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiscountMasterRepository
        extends JpaRepository<DiscountMaster, Long> {

    List<DiscountMaster> findByStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
            DiscountStatus status,
            LocalDate currentDate,
            LocalDate currentDate2
    );
}
