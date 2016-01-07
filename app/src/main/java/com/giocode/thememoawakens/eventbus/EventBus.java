package com.giocode.thememoawakens.eventbus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBus {

    private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    public static Bus getInstance() {
        return BUS;
    }

    private EventBus() {
        // No instances.
    }

    public static void postOnMainThread(final Object event) {
        BUS.post(event);
    }
}
