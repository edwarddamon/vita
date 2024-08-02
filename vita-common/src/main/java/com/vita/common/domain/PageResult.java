package com.vita.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends Result<T> {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer total;

    /**
     * 成功响应
     *
     * @return 响应信息
     */
    public static <T> PageResult<T> success(Integer pageNo, Integer pageSize, Integer total, T data) {
        PageResult<T> pageResult = new PageResult<>();

        pageResult.setSuccess(Boolean.TRUE);
        pageResult.setCode(ResultCode.SUCCESS.getCode());
        pageResult.setMessage(ResultCode.SUCCESS.getMessage());
        pageResult.setData(data);
        pageResult.setPageNum(pageNo);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);

        return pageResult;
    }
}
