package kr.co.kcs.cims.customer;

public enum RepaymentStatus {
    /**
     * 상환이 성공적으로 완료됨.
     */
    SUCCESS,

    /**
     * 상환이 실패함.
     */
    FAILED,

    /**
     * 상환이 아직 처리되지 않은 상태.
     */
    PENDING,

    /**
     * 상환이 일부만 이루어진 상태.
     */
    PARTIALLY_PAID
}
