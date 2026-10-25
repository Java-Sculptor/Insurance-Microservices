package com.logger.agentmodule.repository;
import com.logger.agentmodule.entity.Agent;
import com.logger.agentmodule.enums.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByAgentCode(String agentCode);

    boolean existsByAgentCode(String agentCode);

    List<Agent> findByStatus(AgentStatus status);

    List<Agent> findByAgentCodeContainingIgnoreCase(String agentCode);
    boolean existsByEmail(String email);
}
