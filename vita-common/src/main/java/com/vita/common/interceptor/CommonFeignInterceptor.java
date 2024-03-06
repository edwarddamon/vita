package com.vita.common.interceptor;

import com.vita.common.domain.common.constant.CommonConstant;
import com.vita.common.domain.common.constant.HttpConstant;
import com.vita.common.utils.TokenUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

/**
 * traceId feign 拦截器
 *
 * @author Edward
 * @date 2023-06-17 21:47
 */
public class CommonFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(CommonConstant.TRACE_ID, MDC.get(CommonConstant.TRACE_ID));
        requestTemplate.header(HttpConstant.USER_INFO, TokenUtil.getCurrentUserAsString());
    }
}
