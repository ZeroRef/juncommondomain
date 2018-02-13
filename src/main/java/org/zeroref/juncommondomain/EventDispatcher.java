package org.zeroref.juncommondomain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

public class EventDispatcher {
    private final ListMultimap<Class, Handler> routes = ArrayListMultimap.create();

    public <T extends DomainEvent> void registerHandler(Handler<T> handler, Class<T> c) {
        routes.put(c, handler);
    }

    public void publish(DomainEvent event)
    {
        List<Handler> handlers = routes.get(event.getClass());

        for(Handler handler : handlers)
        {
            handler.handle(event);
        }
    }
}
