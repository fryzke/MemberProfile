package global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 특정 예외(예: 사용자를 찾지 못함) 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // ERROR 레벨 로그와 함께 스택트레이스 기록
        log.error("error: 비즈니스 로직 에러 발생: ", e);
        return ErrorResponse.toResponseEntity(
                HttpStatus.NOT_FOUND,
                "MEMBER_NOT_FOUND",
                e.getMessage()
        );
    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("error: 서버 내부 오류 발생: ", e); // e를 인자로 넘기면 스택트레이스가 출력됨
        return ErrorResponse.toResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SERVER_ERROR",
                "서버 내부 오류가 발생했습니다."
        );
    }
}
