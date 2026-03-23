package com.fit.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @AUTO Bean基类
 * @FILE BaseEntity.java
 * @DATE 2018-3-23 下午2:40:09
 * @Author AIM
 */
@Data
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实体编号（唯一标识）
     */
    protected Long id;

    protected int page = 0;

    protected int limit = 10;

    public BaseEntity() {
    }

    public BaseEntity(Long id) {
        this();
        this.id = id;
    }
}