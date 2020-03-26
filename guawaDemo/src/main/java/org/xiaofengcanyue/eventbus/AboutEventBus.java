package org.xiaofengcanyue.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.swing.event.ChangeEvent;

/**
 * EventBus allows publish-subscribe-style communication between components without requiring the components to explicitly register with one another (and thus be aware of each other)
 * Glossary:
 * Event	          Any object that may be posted to a bus.
 * Subscribing	      The act of registering a listener with an EventBus, so that its handler methods will receive events.
 * Listener	          An object that wishes to receive events, by exposing handler methods.
 * Handler method	  A public method that the EventBus should use to deliver posted events. Handler methods are marked by the @Subscribe annotation.
 * Posting an event	  Making the event available to any listeners through the EventBus.
 */
public class AboutEventBus {

    public static void main(String[] args) {
        EventBus eb = new EventBus();

        //注册
        eb.register(new EventBusChangeRecorder());

        //发布
        eb.post(null);

    }

    static class EventBusChangeRecorder {
        //订阅
        @Subscribe public void recordCustomerChange(ChangeEvent e) {
            e.getSource();
        }

        @Subscribe public void hehe(DeadEvent e){

        }
    }

}
