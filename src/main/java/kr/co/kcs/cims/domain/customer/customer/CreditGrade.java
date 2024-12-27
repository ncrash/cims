package kr.co.kcs.cims.domain.customer.customer;

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
}
