package org.zeroref.juncommondomain.domain;

import org.zeroref.juncommondomain.Handler;

public class RecentItemsProjection implements Handler<InventoryItemCreated> {
    @Override
    public void handle(InventoryItemCreated message) {
        System.out.println("TR " + message.getClass().getName() + " " + message.id);
    }
}
