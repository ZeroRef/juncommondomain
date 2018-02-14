package org.zeroref.juncommondomain.persistence;

import org.zeroref.jpgstreamstore.EventData;
import org.zeroref.jpgstreamstore.EventStore;
import org.zeroref.jpgstreamstore.EventStream;
import org.zeroref.jpgstreamstore.StreamId;
import org.zeroref.juncommondomain.AggregateRoot;
import org.zeroref.juncommondomain.DomainEvent;
import org.zeroref.juncommondomain.Repository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventBasedRepository<T extends AggregateRoot> implements Repository<T>
{
    private final EventStore storage;
    private final AggregateRoot.Factory<T> factory;

    public EventBasedRepository(EventStore storage, Class<T> type)
    {
        this.storage = storage;
        this.factory = new AggregateRoot.Factory<>(type);
    }

    @Override
    public T getById(UUID id)
    {
        T result = factory.newInstance();
        StreamId streamId = new StreamId(id.toString());
        EventStream eventStream = storage.fullEventStreamFor(streamId);

        List<DomainEvent> history = eventStream
                .events().stream().map(e-> (DomainEvent)e.getBody())
                .collect(Collectors.toList());

        result.loadFromHistory(history);
        return result;
    }

    @Override
    public void save(T aggregate, int expectedVersion)
    {
        StreamId streamId = new StreamId(aggregate.getId().toString());

        List<EventData> changeSet = aggregate.getUncommittedChanges()
                .stream().map(uc -> new EventData(uc))
                .collect(Collectors.toList());

        storage.appendToStream(streamId, expectedVersion, changeSet);
    }
}
