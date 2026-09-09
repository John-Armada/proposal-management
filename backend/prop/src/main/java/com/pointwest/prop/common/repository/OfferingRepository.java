package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByActiveTrue();
    Optional<Offering> findByName(String name);
}