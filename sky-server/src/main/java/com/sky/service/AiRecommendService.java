package com.sky.service;
import com.sky.vo.DishVO;
import java.util.List;
import java.util.Map;

public interface AiRecommendService {

    List<DishVO> aiRecommend(Long userId, Integer limit);

    String aiSearchDishes(String keywords, Long categoryId);

    String aiChat(String message, Long userId);

    Map<String, Object> aiAnalyzeUserPreference(Long userId);
}
