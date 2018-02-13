package org.zeroref.juncommondomain;

public interface Handler<T extends DomainEvent>
{
    void handle(T message);
}
