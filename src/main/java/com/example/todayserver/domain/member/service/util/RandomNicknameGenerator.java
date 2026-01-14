package com.example.todayserver.domain.member.service.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Component
public class RandomNicknameGenerator {
    private static final List<String> ADJECTIVES = List.of(
            "행복한", "졸린", "용감한", "귀여운", "차분한", "계획적인", "열정적인"
    );

    private static final List<String> NOUNS = List.of(
            "토끼", "고양이", "여우", "곰", "강아지", "늑대", "다람쥐", "코끼리", "양"
    );

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        String adj = pick(ADJECTIVES);
        String noun = pick(NOUNS);
        int number = random.nextInt(1000);

        return adj + " " +  noun + "_" +  number;
    }

    private String pick(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
