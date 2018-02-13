package org.zeroref.juncommondomain.domain;

import org.zeroref.juncommondomain.core.DomainEvent;

import java.util.UUID;

public class InventoryItemCreated extends DomainEvent
{
    public UUID id;
    public String name;

    public InventoryItemCreated(UUID id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
