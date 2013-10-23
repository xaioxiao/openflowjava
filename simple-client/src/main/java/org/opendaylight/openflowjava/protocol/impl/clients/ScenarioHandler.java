/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.channel.ChannelHandlerContext;

import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author michal.polkorab
 *
 */
public class ScenarioHandler extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioHandler.class);
    private Stack<ClientEvent> scenario;
    private BlockingQueue<byte[]> ofMsg;
    private ChannelHandlerContext ctx;
    private int eventNumber;

    /**
     * 
     * @param scenario
     */
    public ScenarioHandler(Stack<ClientEvent> scenario) {
        this.scenario = scenario;
        ofMsg = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        int freezeCounter = 0;
        while (!scenario.isEmpty()) {
            LOGGER.debug("Running event #" + eventNumber);
            ClientEvent peek = scenario.peek();
            if (peek instanceof WaitForMessageEvent) {
                LOGGER.debug("WaitForMessageEvent");
                try {
                    WaitForMessageEvent event = (WaitForMessageEvent) peek;
                    event.setHeaderReceived(ofMsg.poll(2000, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                    break;
                }
            } else if (peek instanceof SendEvent) {
                LOGGER.debug("Proceed - sendevent");
                SendEvent event = (SendEvent) peek;
                event.setCtx(ctx);
            }
            if (peek.eventExecuted()) {
                scenario.pop();
                eventNumber++;
                freezeCounter = 0;
            } else {
                freezeCounter++;
            }
            if (freezeCounter > 2) {
                LOGGER.warn("Scenario freezed: " + freezeCounter);
                break;
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info("Scenario finished");
        synchronized (this) {
            this.notify();
        }
    }
    
    /**
     * @return true if scenario is done / empty
     */
    public boolean isEmpty() {
        return scenario.isEmpty();
    }

    /**
     * @return scenario
     */
    public Stack<ClientEvent> getScenario() {
        return scenario;
    }

    /**
     * @param scenario scenario filled with desired events
     */
    public void setScenario(Stack<ClientEvent> scenario) {
        this.scenario = scenario;
    }

    /**
     * @param ctx context which will be used for sending messages (SendEvents)
     */
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * @param message received message that is compared to expected message
     */
    public void addOfMsg(byte[] message) {
        ofMsg.add(message);
    }
}
