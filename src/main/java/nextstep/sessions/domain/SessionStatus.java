package nextstep.sessions.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SessionStatus {
    PREPARING("준비중"),
    RECRUITING("모집중"),
    ENDED("종료");

    private static final Map<String, SessionStatus> VALUE_MAP
            = Arrays.stream(SessionStatus.values())
            .collect(Collectors.toUnmodifiableMap(SessionStatus::getName, o -> o));

    private final String name;

    SessionStatus(String name) {
        this.name = name;
    }

    public static SessionStatus find(String name) {
        if (VALUE_MAP.containsKey(name)) {
            return VALUE_MAP.get(name);
        }
        throw new IllegalArgumentException("유요하지 않은 강의상태입니다.(" + name + ")");
    }

    public String getName() {
        return name;
    }

    public boolean isRecruiting() {
        return this == RECRUITING;
    }

}