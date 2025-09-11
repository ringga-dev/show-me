package ngga.ring.myblog.core.base

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

data class BaseResponse<T>(
    var code: Int,
    var status: HttpStatus,
    var message: List<String>,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var data: T? = null,
)


data class BaseResponseList<T>(
    var code: Int,
    var status: HttpStatus,
    var message: List<String>,
    var data: List<T>? = null,
    var meta: MetaData? = null
)


data class MetaData(
    val page: Int? = 0,
    val nextPage: Int? = 0,
    val perPage: Int? = 0,
    val wholePage: Int? = 0
)

data class BaseResponseError(
    var code: Int,
    var status: HttpStatus,
    var message: List<String>,
)