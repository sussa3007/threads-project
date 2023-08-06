package com.challenge.project.threadsbestfollower.service;


import com.challenge.project.threadsbestfollower.dto.UserResponseDto;
import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.generatedata.service.GenerateDataService;
import com.challenge.project.http.dto.FindUserDto;
import com.challenge.project.http.service.ThreadsRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadsBestFollowerService {

    private final ThreadsRankingService threadsRankingService;

    private final GenerateDataService dataService;

    public List<UserResponseDto> getRankingFollower(String username) {
        try {
            List<String> threadsIdList = threadsRankingService.getThreadsIdList(username);
            List<FindUserDto> replyUser = threadsRankingService.getReplyUser(threadsIdList, username);

            List<String> userNameList = replyUser.stream().map(FindUserDto::getUsername).toList();
            Set<String> userNameSet = new HashSet<>(userNameList);

            List<UserResponseDto> userDtoList = setUserResponseListAndReturn(userNameSet, replyUser, userNameList);

            List<UserResponseDto> responseList = setUserResponseHashTagAndRankingCount(userDtoList);

            dataService.setCountPlusOne();
            return responseList;

        } catch (ServiceLogicException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceLogicException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    public String setUserTagGap(List<UserResponseDto> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(u -> sb.append(u.getUsername()).append(" "));
        return sb.toString();
    }

    private List<UserResponseDto> setUserResponseListAndReturn(
            Set<String> userNameSet,
            List<FindUserDto> replyUser,
            List<String> userNameList
    ) {
        List<UserResponseDto> userDtoList = new ArrayList<>();
        for (String setUsername : userNameSet) {
            int frequency = Collections.frequency(userNameList, setUsername);
            String profileUrl = replyUser.stream().filter(ru -> ru.getUsername().equals(setUsername))
                    .findFirst().orElseThrow(RuntimeException::new).getProfileUrl();
            userDtoList.add(UserResponseDto.of(setUsername, profileUrl, (long) frequency));
        }
        return userDtoList;
    }

    private List<UserResponseDto> setUserResponseHashTagAndRankingCount(List<UserResponseDto> userDtoList) {
        List<UserResponseDto> sortedList = userDtoList.stream()
                .sorted(Comparator.comparing(UserResponseDto::getReplyCount).reversed())
                .limit(30)
                .toList();
        return sortedList.stream()
                .peek(ur -> {
                    int i = sortedList.indexOf(ur);
                    ur.setRankingCount(i + 1);
                    String un = ur.getUsername();
                    ur.setUsername("@" + un);
                })
                .toList();
    }
}
