package org.zeroref.juncommondomain;

import java.util.UUID;

public interface Repository<T extends AggregateRoot>
{
    void save(T aggregate, int expectedVersion);
    T getById(UUID id);
}