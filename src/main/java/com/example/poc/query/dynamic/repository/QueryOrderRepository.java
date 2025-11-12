package com.example.poc.query.dynamic.repository;

import com.example.poc.query.dynamic.entity.QueryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueryOrderRepository extends JpaRepository<QueryOrder, UUID> {

    List<QueryOrder> findByContextAndActiveTrue(String context);

    Optional<QueryOrder> findByNameUniqueAndActiveTrue(String nameUnique);
}

