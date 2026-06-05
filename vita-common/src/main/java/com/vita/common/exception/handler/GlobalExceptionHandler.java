package com.vita.common.exception.handler;

import com.vita.common.domain.Result;
import com.vita.common.domain.ResultCode;
import com.vita.common.domain.common.constant.CommonConstant;
import com.vita.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @since 2023/05/30 11:15
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e 异常信息
     * @return 响应信息
     */
    @ExceptionHandler(value = ServiceException.class)
    public Result<Void> serviceExceptionHandler(ServiceException e) {
        log.error("[全局异常处理][ServiceException] 发生业务异常，原因: {}", e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 全局参数异常
     *
     * @param e 异常信息
     * @return 响应信息
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> serviceExceptionHandler(MethodArgumentNotValidException e) {
        log.error("[全局异常处理][参数校验异常处理] 发生参数校验异常，原因: {}", e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors() && !CollectionUtils.isEmpty(bindingResult.getAllErrors())) {
            Set<String> messageSet = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toSet());
            message = StringUtils.join(messageSet, CommonConstant.SEMICOLON);
        }
        Result<Void> failResult = Result.fail(ResultCode.PARAM_CHECK_EXCEPTION.getCode(),
                StringUtils.isNotBlank(message) ? message : ResultCode.PARAM_CHECK_EXCEPTION.getMessage());
        return new ResponseEntity<>(failResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 其他异常
     *
     * @param e 异常信息
     * @return 响应信息
     */
    @ExceptionHandler(value = {Exception.class, Throwable.class})
    public ResponseEntity<Result<Void>> exceptionHandler(Exception e) {
        log.error("[全局异常处理][兜底] 发生异常，原因: {}", e.getMessage(), e);
        return new ResponseEntity<>(Result.fail(ResultCode.FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
