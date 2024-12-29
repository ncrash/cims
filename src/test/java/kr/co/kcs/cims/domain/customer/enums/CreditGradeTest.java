package kr.co.kcs.cims.domain.customer.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CreditGradeTest {

    @Test
    @DisplayName("getGradeNumber: enum 이름에서 숫자만 추출하여 반환한다")
    void getGradeNumber_ShouldExtractNumberFromEnumName() {
        // given & when & then
        assertThat(CreditGrade.GRADE_001.getGradeNumber()).isEqualTo(1);
        assertThat(CreditGrade.GRADE_010.getGradeNumber()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("findByGrade: 유효한 등급 번호로 CreditGrade를 찾을 수 있다")
    void findByGrade_ShouldReturnCorrectGrade(int grade) {
        // when
        CreditGrade result = CreditGrade.findByGrade(grade);

        // then
        assertThat(result.getGradeNumber()).isEqualTo(grade);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, -1})
    @DisplayName("findByGrade: 유효하지 않은 등급 번호로 조회시 예외가 발생한다")
    void findByGrade_ShouldThrowException_WhenGradeIsInvalid(int invalidGrade) {
        // when & then
        assertThatThrownBy(() -> CreditGrade.findByGrade(invalidGrade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grade must be between 1 and 10");
    }
}
