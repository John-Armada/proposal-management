package com.pointwest.prop.review.repository;
import com.pointwest.prop.common.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ReviewRepository extends JpaRepository<Review, Long>{
    
}
