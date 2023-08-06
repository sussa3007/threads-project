package com.challenge.project.threadsbestfollower.controller;

import com.challenge.project.threadsbestfollower.dto.CreateForm;
import com.challenge.project.threadsbestfollower.dto.CreateViewResponseDto;
import com.challenge.project.threadsbestfollower.dto.UserRequestDto;
import com.challenge.project.threadsbestfollower.dto.UserResponseDto;
import com.challenge.project.threadsbestfollower.service.ThreadsBestFollowerService;
import com.challenge.project.exception.CustomBindException;
import com.challenge.project.file.service.FileService;
import com.challenge.project.generatedata.service.GenerateDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BestFollowerController {

    private final ThreadsBestFollowerService service;

    private final FileService fileService;

    private final GenerateDataService dataService;

    @GetMapping
    public String home(
            Model model
    ) {
        Long count = dataService.getCountingData();

        model.addAttribute("createForm", new CreateForm());
        model.addAttribute("countMessage", "지금까지 " + count + "명. 비공개 프로필 외않되? 하지마");
        return "index";
    }

    @PostMapping("loading")
    public ModelAndView loading(
            @ModelAttribute UserRequestDto dto,
            @Valid CreateForm questionForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomBindException(bindingResult);
        }
        return new ModelAndView("loading",
                Map.of(
                        "msg", "너한테 관심 있는 30명 찾는중.... 20초 걸려... 확인/닫기 누르고 얌전히 기다려❗❗",
                        "nextPage",
                        "/ranking?username=" + dto.getUsername()
                )
        );
    }

    @GetMapping("ranking")
    public ModelAndView createRanking(
            @RequestParam Map<String, Object> param
    ) {
        String username = (String) param.get("username");
        List<UserResponseDto> rankingFollower = service.getRankingFollower(username.toLowerCase().replaceAll(" ", ""));
        List<UserResponseDto> list = fileService.imageLocalDownloader(rankingFollower);
        Long count = dataService.getCountingData();
        String info = service.setUserTagGap(list);
        dataService.createGenerateData(username, info);
        CreateViewResponseDto response = CreateViewResponseDto.builder()
                .data(list)
                .info(info)
                .counting(count)
                .build();

        return new ModelAndView(
                "create_ranking",
                Map.of(
                        "response", response,
                        "createForm", new CreateForm()
                )
        );
    }
}
