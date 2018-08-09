package com.gwg.mq.server;

import com.gwg.mq.server.netty.ResponseFuture;

public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
