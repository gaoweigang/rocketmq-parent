package com.gwg.mq.server;

import com.gwg.mq.server.netty.exception.RemotingCommandException;

public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;
}
