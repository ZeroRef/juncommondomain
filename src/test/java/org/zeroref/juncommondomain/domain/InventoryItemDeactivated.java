package org.zeroref.juncommondomain.domain;

import org.zeroref.juncommondomain.DomainEvent;

import java.util.UUID;

public class InventoryItemDeactivated extends DomainEvent {
    public final UUID id;

    public InventoryItemDeactivated(UUID id)
    {
        this.id = id;
    }
}
