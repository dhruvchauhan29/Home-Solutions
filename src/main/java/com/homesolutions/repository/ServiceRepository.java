package com.homesolutions.repository;

import com.homesolutions.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    @Query("SELECT s FROM Service s WHERE " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "s.active = true")
    Page<Service> searchServices(
        @Param("categoryId") Long categoryId,
        @Param("search") String search,
        Pageable pageable
    );
}
