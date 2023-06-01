package nextstep.courses.domain;

import nextstep.users.domain.NsUserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static nextstep.Fixtures.aSession;
import static nextstep.Fixtures.aSessionRegistrationBuilder;
import static org.assertj.core.api.Assertions.*;

class SessionTest {

    @DisplayName("강의는 시작일과 종료일을 가진다.")
    @Test
    void 시작일_종료일_확인() {
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusDays(1);
        Session session = aSession().withSessionPeriod(new SessionPeriod(startedAt, endedAt)).build();

        assertThat(session.startedAt()).isEqualTo(startedAt);
        assertThat(session.endedAt()).isEqualTo(endedAt);
    }

    @DisplayName("강의는 강의 커버 이미지 정보를 가진다.")
    @Test
    void 강의_커버_이미지경로_확인() {
        String sessionCoverImage = "https://edu.nextstep.camp/images/pages/course/syllabus/line_02.png";
        Session session = aSession().withSessionCoverImage(sessionCoverImage).build();
        assertThat(session.getSessionCoverImage()).isEqualTo(sessionCoverImage);
    }

    @DisplayName("강의는 무료 강의와 유료 강의로 나뉜다.")
    @ParameterizedTest
    @EnumSource(value = SessionCostType.class, names = {"FREE", "PAID"})
    void 강의_타입_확인(SessionCostType sessionCostType) {
        Session session = aSession().withSessionCostType(sessionCostType).build();
        assertThat(session.getSessionCostType()).isEqualTo(sessionCostType);
    }

    @DisplayName("강의 상태는 준비중, 모집중, 종료 3가지 상태를 가진다.")
    @ParameterizedTest
    @EnumSource(value = SessionStatus.class, names = {"PREPARING", "RECRUITING", "CLOSED"})
    void 강의_상태_확인(SessionStatus sessionStatus) {
        Session session = aSession()
                .withSessionRegistration(aSessionRegistrationBuilder()
                        .withSessionStatus(sessionStatus)
                        .build())
                .build();
        assertThat(session.getSessionStatus()).isEqualTo(sessionStatus);
    }

    @DisplayName("강의 상태가 모집중이 아니면, 수강신청이 불가능")
    @ParameterizedTest
    @EnumSource(value = SessionStatus.class, names = {"PREPARING", "CLOSED"})
    void 수강신청_모집중아닌경우_불가능(SessionStatus sessionStatus) {
        Session session = aSession()
                .withSessionRegistration(aSessionRegistrationBuilder()
                        .withSessionStatus(sessionStatus)
                        .build())
                .build();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> session.register(NsUserTest.JAVAJIGI))
                .withMessageMatching("해당 강의는 모집중이 아닙니다.");
    }

    @DisplayName("강의 수강신청은 강의 상태가 모집중일 때만 가능하다.")
    @ParameterizedTest
    @EnumSource(value = SessionStatus.class, names = {"RECRUITING"})
    void 수강신청_모집중_가능(SessionStatus sessionStatus) {
        Session session = aSession()
                .withSessionRegistration(aSessionRegistrationBuilder()
                        .withUser(NsUserTest.JAVAJIGI)
                        .withSessionStatus(sessionStatus)
                        .build())
                .build();

        assertThat(session.getUsers()).hasSize(1).containsOnly(NsUserTest.JAVAJIGI);
    }

    @DisplayName("강의는 강의 최대 수강 인원을 초과할 수 없다.")
    @Test
    void 수강신청_수강인원초과_불가능() {
        final int maxUserCount = 1;
        Session session = aSession()
                .withSessionRegistration(aSessionRegistrationBuilder()
                        .withUser(NsUserTest.JAVAJIGI)
                        .withMaxUserCount(maxUserCount)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> session.register(NsUserTest.SANJIGI))
                .withMessageMatching("최대 수강 인원을 초과했습니다.");
    }

    @DisplayName("중복 신청된 경우 예외처리")
    @Test
    void 수강신청_중복신청된_경우() {
        Session session = aSession()
                .withSessionRegistration(aSessionRegistrationBuilder()
                        .withUser(NsUserTest.JAVAJIGI)
                        .withUser(NsUserTest.SANJIGI)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> session.register(NsUserTest.SANJIGI))
                .withMessageMatching("이미 등록 되었습니다.");
    }
}

