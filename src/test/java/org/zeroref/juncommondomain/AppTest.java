package org.zeroref.juncommondomain;

import org.junit.Test;
import org.zeroref.jpgstreamstore.storage.PgEventStorage;
import org.zeroref.juncommondomain.core.Repository;
import org.zeroref.juncommondomain.domain.InventoryItem;
import org.zeroref.juncommondomain.persistence.EventBasedRepository;

import java.io.IOException;
import java.util.UUID;

public class AppTest
{
    private static String connectionString = "jdbc:postgresql://localhost:5432/juncommondomain";

    @Test
    public void test() throws IOException {
        PgEventStorage eventStore = new PgEventStorage(connectionString);
        eventStore.createSchema();

        Repository<InventoryItem> repository = new EventBasedRepository<>(eventStore, InventoryItem.class);

        UUID uuid = UUID.randomUUID();
        InventoryItem t1 = new InventoryItem(uuid, "Item 1");
        repository.save(t1, 1);

        InventoryItem t2 = repository.getById(uuid);
        t2.deactivate();
        repository.save(t2, t2.getVersion());

        System.out.println( "Done, yay!" );
    }
}
