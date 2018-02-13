package org.zeroref.juncommondomain.domain;

import org.zeroref.juncommondomain.core.DomainEvent;

import java.util.UUID;

public class InventoryItemRenamed extends DomainEvent {
    public final UUID id;
    public final String newName;

    public InventoryItemRenamed(UUID id, String newName)
    {
        this.id = id;
        this.newName = newName;
    }
}
