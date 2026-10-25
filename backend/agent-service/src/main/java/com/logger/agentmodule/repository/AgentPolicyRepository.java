package com.logger.agentmodule.repository;

import com.logger.agentmodule.entity.AgentPolicy;
import com.logger.agentmodule.enums.AgentPolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentPolicyRepository
        extends JpaRepository<AgentPolicy, Long> {

    List<AgentPolicy> findByAgentId(Long agentId);

    List<AgentPolicy> findByCustomerId(Long customerId);

    Optional<AgentPolicy> findByAgentIdAndPolicyId(
            Long agentId,
            Long policyId
    );

    List<AgentPolicy> findByAgentIdAndStatus(
            Long agentId,
            AgentPolicyStatus status
    );



}

