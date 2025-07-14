package com.jandira.learningplatform.repository;

import com.jandira.learningplatform.model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tutorial> searchByTitle(@Param("query") String query);
}
