package com.pointwest.prop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointwest.prop.common.entity.RevokedToken;

@Repository 
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String>{
    
}
