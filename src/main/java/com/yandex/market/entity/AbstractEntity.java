package com.yandex.market.entity;

import org.hibernate.engine.spi.ManagedMappedSuperclass;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable, ManagedMappedSuperclass {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

}
