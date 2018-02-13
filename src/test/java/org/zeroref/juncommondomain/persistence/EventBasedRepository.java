package org.zeroref.juncommondomain.persistence;

import org.zeroref.jpgstreamstore.EventData;
import org.zeroref.jpgstreamstore.EventStore;
import org.zeroref.jpgstreamstore.EventStream;
import org.zeroref.jpgstreamstore.StreamId;
import org.zeroref.juncommondomain.core.AggregateRoot;
import org.zeroref.juncommondomain.core.DomainEvent;
import org.zeroref.juncommondomain.core.Repository;
import shaded.com.google.gson.Gson;
import shaded.com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventBasedRepository<T extends AggregateRoot> implements Repository<T>
{
    private final EventStore storage;
    private final AggregateRoot.Factory<T> factory;
    private static Gson serializer = new GsonBuilder().create();

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
                .events().stream().map(EventBasedRepository::unwrap)
                .collect(Collectors.toList());

        result.loadFromHistory(history);
        return result;
    }

    @Override
    public void save(T aggregate, int expectedVersion)
    {
        StreamId streamId = new StreamId(aggregate.getId().toString());

        List<EventData> changeSet = aggregate.getUncommittedChanges()
                .stream().map(EventBasedRepository::wrap)
                .collect(Collectors.toList());

        storage.appendToStream(streamId, expectedVersion, changeSet);
    }

    private static EventData wrap(DomainEvent evt) {
        HashMap<String, String> props = new HashMap<>();
        props.put("type", evt.getClass().getName());
        props.put("data", serializer.toJson(evt));

        return new EventData(props);
    }

    private static DomainEvent unwrap(EventData ed) {
        Map<String, String> dataProps = ed.getProps();

        String data = dataProps.get("data");

        String type = dataProps.get("type");
        Class<DomainEvent> eventClass;
        try {
            eventClass = (Class<DomainEvent>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            throw new IllegalStateException("Unable to load type: " + type);
        }

        return serializer.fromJson(data, eventClass);
    }
}
