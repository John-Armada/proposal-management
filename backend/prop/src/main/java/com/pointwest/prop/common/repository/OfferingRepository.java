package com.pointwest.prop.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointwest.prop.common.entity.Offering;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByActiveTrue();
}