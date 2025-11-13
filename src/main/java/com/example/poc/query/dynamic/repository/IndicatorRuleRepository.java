package com.example.poc.query.dynamic.repository;

import com.example.poc.query.dynamic.entity.IndicatorRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IndicatorRuleRepository extends JpaRepository<IndicatorRule, UUID> {

    List<IndicatorRule> findByContextAndActiveTrueOrderByDisplayOrderAsc(String context);

    List<IndicatorRule> findByContextOrderByDisplayOrderAsc(String context);

    List<IndicatorRule> findByActiveTrueOrderByDisplayOrderAsc();

    List<IndicatorRule> findAllByOrderByDisplayOrderAsc();
}

