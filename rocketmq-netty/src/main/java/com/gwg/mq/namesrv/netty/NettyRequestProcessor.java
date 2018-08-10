package com.gwg.mq.namesrv.netty;

import com.gwg.mq.namesrv.netty.protocol.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * Common remoting command processor
 */
public interface NettyRequestProcessor {
    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
        throws Exception;

    boolean rejectRequest();
}
