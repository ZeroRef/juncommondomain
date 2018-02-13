package org.zeroref.juncommondomain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {
    private List<DomainEvent> changes = new ArrayList<>();
    public abstract UUID getId();
    private int version = 0;

    public int getVersion() {
        return version;
    }

    protected abstract void apply(DomainEvent e);

    protected void applyChange(DomainEvent e)
    {
        applyChange(e, true);
    }

    private void applyChange(DomainEvent e, boolean isNew)
    {
        apply(e);
        if (isNew)
        {
            changes.add(e);
        }

        version++;
    }

    public List<DomainEvent> getUncommittedChanges()
    {
        return changes;
    }

    public void loadFromHistory(List<DomainEvent> history)
    {
        for(DomainEvent e: history) applyChange(e, false);
    }

    public static class Factory<T>
    {
        private Class<T> clazz;

        public Factory(Class<T> clazz)
        {
            this.clazz = clazz;
        }

        public T newInstance()
        {
            try
            {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException("Unable to create an instance of " + clazz.getName(), e);
            }
        }
    }
}
