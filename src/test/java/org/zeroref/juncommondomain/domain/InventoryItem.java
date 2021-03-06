package org.zeroref.juncommondomain.domain;

import org.zeroref.juncommondomain.AggregateRoot;
import org.zeroref.juncommondomain.DomainEvent;

import java.util.UUID;

public class InventoryItem extends AggregateRoot
{
    private UUID id;
    private String name;
    private boolean activated;

    public InventoryItem() {
        // used to create in repository ... many ways to avoid this, eg making private constructor
    }

    public InventoryItem(UUID id, String name)
    {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        applyChange(new InventoryItemCreated(id, name));
    }



    @Override
    public UUID getId() {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void rename(String newName)
    {
        if ((newName == null && newName.isEmpty()) || newName.trim().isEmpty())  throw new IllegalArgumentException("A new name must be provided");
        applyChange(new InventoryItemRenamed(id, newName));
    }

    public void deactivate()
    {
        if(!activated) throw new IllegalStateException("The item already deactivated");
        applyChange(new InventoryItemDeactivated(id));
    }

    @Override
    protected void apply(DomainEvent e)
    {
        if (e instanceof InventoryItemCreated)
        {
            apply((InventoryItemCreated) e);
        }
        else if(e instanceof InventoryItemRenamed)
        {
            apply((InventoryItemRenamed) e);
        }
        else if(e instanceof InventoryItemDeactivated)
        {
            apply((InventoryItemDeactivated) e);
        }
        else
            throw new UnsupportedOperationException(e + " is not supported " + this);
    }

    private void apply(InventoryItemCreated e)
    {
        id = e.id;
        name = e.name;
        activated = true;
    }

    private void apply(InventoryItemRenamed e)
    {
        name = e.newName;
    }

    private void apply(InventoryItemDeactivated e)
    {
        activated = false;
    }
}
