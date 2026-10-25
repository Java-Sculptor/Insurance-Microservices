package com.logger.agentmodule.repository;
import com.logger.agentmodule.entity.AgentCustomer;
import com.logger.agentmodule.enums.AgentCustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentCustomerRepository
        extends JpaRepository<AgentCustomer, Long> {

    List<AgentCustomer> findByAgentId(Long agentId);

    List<AgentCustomer> findByCustomerId(Long customerId);

    List<AgentCustomer> findByAgentIdAndStatus(
            Long agentId,
            AgentCustomerStatus status
    );
    Optional<AgentCustomer> findByAgentIdAndCustomerIdAndStatus(
            Long agentId,
            Long customerId,
            AgentCustomerStatus status
    );
}

