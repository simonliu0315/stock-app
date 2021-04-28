package com.stock.app.repository;

import com.stock.app.domain.MainStock;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the MainStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MainStockRepository extends JpaRepository<MainStock, Long> {
}
