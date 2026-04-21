package com.sky.controller.user;
import com.sky.context.BaseContext;
import com.sky.dto.AiChatDTO;
import com.sky.result.Result;
import com.sky.service.AiRecommendService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("userAiController")
@RequestMapping("/user/ai")
@Slf4j
@Api(tags = "C端-AI智能服务接口")
public class AiController {

    @Autowired
    private AiRecommendService aiRecommendService;

    @GetMapping("/recommend")
    @ApiOperation("AI智能推荐菜品")
    public Result<List<DishVO>> aiRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = BaseContext.getCurrentId();
        List<DishVO> list = aiRecommendService.aiRecommend(userId, limit);
        return Result.success(list);
    }

    @GetMapping("/search")
    @ApiOperation("AI语义搜索菜品")
    public Result<String> aiSearch(@RequestParam String keywords,
                                    @RequestParam(required = false) Long categoryId) {
        String result = aiRecommendService.aiSearchDishes(keywords, categoryId);
        return Result.success(result);
    }

    @PostMapping("/chat")
    @ApiOperation("AI智能客服对话")
    public Result<String> aiChat(@RequestBody AiChatDTO dto) {
        Long userId = BaseContext.getCurrentId();
        String response = aiRecommendService.aiChat(dto.getMessage(), userId);
        return Result.success(response);
    }

    @GetMapping("/preference")
    @ApiOperation("AI分析用户偏好")
    public Result<Map<String, Object>> analyzePreference() {
        Long userId = BaseContext.getCurrentId();
        Map<String, Object> preference = aiRecommendService.aiAnalyzeUserPreference(userId);
        return Result.success(preference);
    }
}
