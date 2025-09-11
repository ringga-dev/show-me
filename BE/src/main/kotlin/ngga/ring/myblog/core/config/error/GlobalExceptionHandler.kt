package ngga.ring.myblog.core.config.error


import jakarta.validation.ConstraintViolationException
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.base.BaseResponseError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException

class ResourceNotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : RuntimeException(message)
class InvalidUserException(message: String) : RuntimeException(message)
class DataNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String, val error: Any? = null) : RuntimeException(message)




@ControllerAdvice
class GlobalExceptionHandler {

    // Seringnya Spring lempar MultipartException yang membungkus MaxUploadSizeExceededException
    @ExceptionHandler(MultipartException::class)
    fun handleMultipart(e: MultipartException): ResponseEntity<BaseResponseError> {
        val isMax = e.cause is MaxUploadSizeExceededException
        val status = if (isMax) HttpStatus.PAYLOAD_TOO_LARGE else HttpStatus.BAD_REQUEST
        val msg = if (isMax) "Maximum upload size exceeded" else (e.message ?: "Multipart error")

        return ResponseEntity.status(status).body(
            BaseResponseError(
                code = status.value(),
                status = status,
                message = arrayListOf(msg)
            )
        )
    }

    // Jaga-jaga kalau dilempar langsung tanpa dibungkus
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSize(e: MaxUploadSizeExceededException): ResponseEntity<BaseResponseError> {
        val status = HttpStatus.PAYLOAD_TOO_LARGE
        return ResponseEntity.status(status).body(
            BaseResponseError(
                code = status.value(),
                status = status,
                message = arrayListOf("Maximum upload size exceeded")
            )
        )
    }

    // Handler kamu yang sudah ada untuk bad request lain
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<BaseResponseError> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            BaseResponseError(
                code = status.value(),
                status = status,
                message = arrayListOf(e.message.orEmpty())
            )
        )
    }
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<BaseResponseError> {

        return ResponseEntity(BaseResponseError(
            code = HttpStatus.FORBIDDEN.value(),
            status = HttpStatus.FORBIDDEN,
            message = arrayListOf(ex.message ?: "Access Denied")
        ), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<BaseResponseError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(
            BaseResponseError(
                code = status.value(),
                status = status,
                message = arrayListOf(ex.message ?: "Internal Server Error")
            ),
            status
        )
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): BaseResponseError {
        return BaseResponseError(
            code = HttpStatus.BAD_REQUEST.value(),
            status = HttpStatus.BAD_REQUEST,
            message = arrayListOf(ex.message ?: "Validation error"),
        )
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun handleValidationErrors(constraintViolationException: ConstraintViolationException): BaseResponseError {
        val errors = constraintViolationException.constraintViolations.map { it.message }
        return BaseResponseError(
            code = 400,
            status = HttpStatus.BAD_REQUEST,
            message = errors,
        )
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<BaseResponseError> {
        val errorResponse = BaseResponseError(
            code = HttpStatus.NOT_FOUND.value(),
            status = HttpStatus.NOT_FOUND,
            message = listOf(ex.message ?: "User not found"),
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InvalidUserException::class)
    fun handleInvalidPasswordException(ex: InvalidUserException): ResponseEntity<BaseResponseError> {
        val errorResponse = BaseResponseError(
            code = HttpStatus.UNAUTHORIZED.value(),
            status = HttpStatus.UNAUTHORIZED,
            message = listOf(ex.message ?: "Invalid password"),
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(DataNotFoundException::class)
    fun handleDataNotFoundException(ex: DataNotFoundException): ResponseEntity<BaseResponseError> {
        val errorResponse = BaseResponseError(
            code = HttpStatus.NOT_FOUND.value(),
            status = HttpStatus.NOT_FOUND,
            message = listOf(ex.message ?: "Data not found"),
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<BaseResponseError> {
        return ResponseEntity(
            BaseResponseError(
                code = HttpStatus.UNAUTHORIZED.value(),
                status = HttpStatus.UNAUTHORIZED,
                message = listOf(ex.message ?: "Data not found"),
            ),
            HttpStatus.UNAUTHORIZED
        )
    }


}