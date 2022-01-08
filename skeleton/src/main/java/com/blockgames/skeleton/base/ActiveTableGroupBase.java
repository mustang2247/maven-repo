package com.blockgames.skeleton.base;

import com.blockgames.skeleton.arch.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * @author mustangkong
 */
public class ActiveTableGroupBase {

    private EventLoop eventLoop;

    public ActiveTableGroupBase(long interval, TimeUnit tu) {
        eventLoop = new EventLoop();
    }

    public void addTable(TableBase table) {
        eventLoop.register(table);
    }

    public void removeTable(TableBase table) {
        eventLoop.unregister(table);
    }

}
