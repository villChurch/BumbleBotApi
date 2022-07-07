package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Integer> {
}
