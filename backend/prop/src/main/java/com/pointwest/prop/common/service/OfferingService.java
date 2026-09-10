package com.pointwest.prop.common.service;

import org.springframework.stereotype.Service;

import com.pointwest.prop.common.repository.OfferingRepository;
import com.pointwest.prop.common.entity.Offering;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class OfferingService {
    private final OfferingRepository offeringRepository;

    @Transactional 
    public Offering findById(Long id){
        return offeringRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));
    }

}
