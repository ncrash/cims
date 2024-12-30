package kr.co.kcs.cims.domain.customer.enums;

import java.util.Arrays;

public enum CreditGrade {
    GRADE_001,
    GRADE_002,
    GRADE_003,
    GRADE_004,
    GRADE_005,
    GRADE_006,
    GRADE_007,
    GRADE_008,
    GRADE_009,
    GRADE_010,
    ;

    public int getGradeNumber() {
        String numberOnly = this.name().replaceAll("\\D", "");
        return numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);
    }

    public static CreditGrade findByGrade(int grade) {
        if (grade > 10 || grade < 1) {
            throw new IllegalArgumentException("Grade must be between 1 and 10 : " + grade);
        }

        return Arrays.stream(CreditGrade.values())
                .filter(creditGrade -> creditGrade.name().endsWith(String.valueOf(grade)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid grade: " + grade));
    }
}
