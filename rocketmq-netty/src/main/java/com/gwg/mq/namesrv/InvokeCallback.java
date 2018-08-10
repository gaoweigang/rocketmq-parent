package com.gwg.mq.namesrv;

import com.gwg.mq.namesrv.netty.ResponseFuture;

public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
