package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.Offering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
	Optional<Offering> findByName(String name);
}