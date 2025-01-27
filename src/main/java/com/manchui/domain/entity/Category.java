package com.manchui.domain.entity;

public enum Category {
    ALL("전체", "all"),
    DEVELOP("개발", "develop"),
    HEALTH("운동", "health"),
    MOVIE("영화", "movie"),
    STUDY("공부", "study"),
    CULTURE_ART("문화/예술", "cultureart"),
    GAME("게임", "game"),
    TRAVEL("여행", "travel"),
    FOOD("맛집", "food"),
    MUSIC("음악", "music");

    private final String korean;
    private final String english;

    Category(String korean, String english) {
        this.korean = korean;
        this.english = english;
    }

    public static String toKorean(String english) {
        for (Category category : values()) {
            if (category.english.equalsIgnoreCase(english)) {
                return category.korean;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + english);
    }
}
