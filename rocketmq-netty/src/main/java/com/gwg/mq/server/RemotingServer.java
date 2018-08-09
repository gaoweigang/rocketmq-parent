package com.gwg.mq.server;

import java.util.concurrent.ExecutorService;

import com.gwg.mq.server.netty.NettyRequestProcessor;
import com.gwg.mq.server.netty.common.Pair;
import com.gwg.mq.server.netty.exception.RemotingSendRequestException;
import com.gwg.mq.server.netty.exception.RemotingTimeoutException;
import com.gwg.mq.server.netty.exception.RemotingTooMuchRequestException;
import com.gwg.mq.server.netty.protocol.RemotingCommand;

import io.netty.channel.Channel;

public interface RemotingServer extends RemotingService {

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
        final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
        RemotingTimeoutException;

    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
        RemotingSendRequestException;

}
