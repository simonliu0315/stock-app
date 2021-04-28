package com.stock.app.repository;

import com.stock.app.domain.DailyPrice;
import com.stock.app.domain.TWT38U;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the MainStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TWT38URepository extends JpaRepository<TWT38U, Long> {

    //@Query("SELECT s.cardNumber FROM DailyPrice s where s.no =:no and s.date = :date   ")
    public TWT38U findByNoAndDate(String no, String date);


}
