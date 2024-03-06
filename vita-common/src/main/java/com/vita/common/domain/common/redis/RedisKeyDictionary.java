package com.vita.common.domain.common.redis;

/**
 * @author Edward
 * @date 2023-06-17 18:44
 */
public class RedisKeyDictionary {

    /**
     * 不需要鉴权的接口
     */
    public static final String NOT_NEED_AUTH_INTERFACE = "user:not:auth:interface";

    /**
     * 登录用户的token
     */
    public static final String USER_TOKEN = "user:token:";

    /**
     * 在线用户<p/>
     * key: 用户Id<p/>
     * value: 用户token
     */
    public static final String USER_ONLINE_ID = "user:online:";

}
