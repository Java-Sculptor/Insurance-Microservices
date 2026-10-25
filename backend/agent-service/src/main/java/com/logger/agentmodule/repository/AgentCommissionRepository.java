package com.logger.agentmodule.repository;
import com.logger.agentmodule.entity.AgentCommission;
import com.logger.agentmodule.enums.AgentCommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgentCommissionRepository
        extends JpaRepository<AgentCommission, Long> {

    List<AgentCommission> findByAgentId(Long agentId);

    List<AgentCommission> findByStatus(
            AgentCommissionStatus status
    );

    List<AgentCommission> findByAgentIdAndStatus(
            Long agentId,
            AgentCommissionStatus status
    );

    List<AgentCommission> findByEarnedDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<AgentCommission> findByAgentIdAndEarnedDateBetween(
            Long agentId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}

