package com.gwg.mq.namesrv;

import com.gwg.mq.namesrv.netty.exception.RemotingCommandException;

public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;
}
