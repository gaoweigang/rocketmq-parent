package com.gwg.mq.namesrv;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.gwg.mq.namesrv.netty.NettyRequestProcessor;
import com.gwg.mq.namesrv.netty.exception.RemotingConnectException;
import com.gwg.mq.namesrv.netty.exception.RemotingSendRequestException;
import com.gwg.mq.namesrv.netty.exception.RemotingTimeoutException;
import com.gwg.mq.namesrv.netty.exception.RemotingTooMuchRequestException;
import com.gwg.mq.namesrv.netty.protocol.RemotingCommand;

public interface RemotingClient extends RemotingService {

    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    RemotingCommand invokeSync(final String addr, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingConnectException,
        RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
        RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
        final ExecutorService executor);

    void setCallbackExecutor(final ExecutorService callbackExecutor);

    boolean isChannelWritable(final String addr);
}
