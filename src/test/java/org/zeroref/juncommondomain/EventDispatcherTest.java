package org.zeroref.juncommondomain;

import org.junit.Test;
import org.zeroref.jpgstreamstore.EventData;
import org.zeroref.jpgstreamstore.EventStream;
import org.zeroref.jpgstreamstore.StoreRecord;
import org.zeroref.jpgstreamstore.StreamId;
import org.zeroref.jpgstreamstore.storage.PgEventStorage;
import org.zeroref.juncommondomain.domain.InventoryItem;
import org.zeroref.juncommondomain.domain.InventoryItemCreated;
import org.zeroref.juncommondomain.domain.RecentItemsService;
import org.zeroref.juncommondomain.persistence.EventBasedRepository;

import java.io.IOException;
import java.util.UUID;

public class EventDispatcherTest
{
    private static String connectionString = "jdbc:postgresql://localhost:5432/juncommondomain";

    @Test
    public void test() throws IOException {
        PgEventStorage eventStore = new PgEventStorage(connectionString);
        eventStore.advanced().createSchema();

        Repository<InventoryItem> repository = new EventBasedRepository<>(eventStore, InventoryItem.class);

        UUID uuid = UUID.randomUUID();
        InventoryItem t1 = new InventoryItem(uuid, "Item 1");
        repository.save(t1, 1);

        InventoryItem t2 = repository.getById(uuid);
        t2.deactivate();
        repository.save(t2, t2.getVersion());

        System.out.println( "Done, yay!" );


        RecentItemsService service = new RecentItemsService();

        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.registerHandler(service, InventoryItemCreated.class);


        for(EventData e : eventStore.eventsSince(0)){
            dispatcher.publish((DomainEvent) e.getBody());
        }
    }
}
