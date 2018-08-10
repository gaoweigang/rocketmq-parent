package com.gwg.mq.namesrv;
import com.gwg.mq.namesrv.netty.protocol.RemotingCommand;

public interface RPCHook {
    void doBeforeRequest(final String remoteAddr, final RemotingCommand request);

    void doAfterResponse(final String remoteAddr, final RemotingCommand request,
        final RemotingCommand response);
}
