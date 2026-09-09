package com.pointwest.prop.templates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointwest.prop.common.entity.Template;

@Repository 
public interface TemplateRepository extends JpaRepository<Template, Long> {
}
