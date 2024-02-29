package com.edstem.taxibookingandbillingsystem.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    private final String entity;
    private final Long id;

    public EntityAlreadyExistsException(String entity) {
        super("User already exist");
        this.entity = entity;
        this.id = 0L;
    }
}
