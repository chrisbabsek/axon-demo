package de.babsek.demo.axontesting.remote

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun on(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = exception.bindingResult.fieldErrors
            .joinToString(separator = ", ") { error ->
                "${error.field}: ${error.defaultMessage ?: "invalid"}"
            }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    errorType = "ValidationFailed",
                    errorMessage = details,
                ),
            )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun on(exception: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val details = exception.constraintViolations
            .joinToString(separator = ", ") { violation ->
                "${violation.propertyPath}: ${violation.message}"
            }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    errorType = "ConstraintViolation",
                    errorMessage = details,
                ),
            )
    }

    @ExceptionHandler(RuntimeException::class)
    fun on(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    errorType = exception::class.java.simpleName,
                    errorMessage = exception.message,
                ),
            )
    }

    data class ErrorResponse(
        val errorType: String,
        val errorMessage: String?,
    )
}
