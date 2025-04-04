
package com.bank.mqmanagement.repository;

import com.bank.mqmanagement.model.Partner;
import com.bank.mqmanagement.model.Partner.Direction;
import com.bank.mqmanagement.model.Partner.ProcessedFlowType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByAlias(String alias);

    boolean existsByAlias(String alias);

    Page<Partner> findAllByOrderByAliasAsc(Pageable pageable);

    List<Partner> findByDirection(Direction direction);

    List<Partner> findByProcessedFlowType(ProcessedFlowType processedFlowType);

    @Query("SELECT p FROM Partner p WHERE LOWER(p.alias) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.type) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.application) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Partner> search(String keyword, Pageable pageable);
}