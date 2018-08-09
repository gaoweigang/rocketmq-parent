package com.gwg.mq.server.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gwg.mq.server.RPCHook;
import com.gwg.mq.server.netty.common.TlsHelper;
import com.gwg.mq.server.netty.common.TlsMode;
import com.gwg.mq.server.netty.protocol.RemotingCommand;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class NettyRemotingServer extends NettyRemotingAbstract{
	
    public NettyRemotingServer(int permitsOneway, int permitsAsync) {
		super(permitsOneway, permitsAsync);
	}

	private static final Logger log = LoggerFactory.getLogger(NettyRemotingServer.class);

	/**
	 * 服务端监听的端口地址
	 */
	private static final int port = 7878;
	
    private static final String HANDSHAKE_HANDLER_NAME = "handshakeHandler";
    private static final String TLS_HANDLER_NAME = "sslHandler";
    private static final String FILE_REGION_ENCODER_NAME = "fileRegionEncoder";

    private DefaultEventExecutorGroup defaultEventExecutorGroup;
   	
    private ChannelEventListener channelEventListener;
    
	public static void main(String[] args) {
	  
	}
    
    public void start(){
    	channelEventListener = new ChannelEventListener() {
			
			@Override
			public void onChannelIdle(String remoteAddr, Channel channel) {
				
			}
			
			@Override
			public void onChannelException(String remoteAddr, Channel channel) {
				
			}
			
			@Override
			public void onChannelConnect(String remoteAddr, Channel channel) {
				
			}
			
			@Override
			public void onChannelClose(String remoteAddr, Channel channel) {
				
			}
		};
		
		defaultEventExecutorGroup = new DefaultEventExecutorGroup(8, new ThreadFactory() {
	                private AtomicInteger threadIndex = new AtomicInteger(0);

	                public Thread newThread(Runnable r) {
	                    return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
	                }
	            });
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
            }
        });
		
		
		EventLoopGroup workerGroup = new NioEventLoopGroup(3, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            private int threadTotal = 3;

            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
            }
        });
		try {
			
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
		     .channel(NioServerSocketChannel.class)
		     .option(ChannelOption.SO_BACKLOG, 1024)
		     .option(ChannelOption.SO_REUSEADDR, true)
		     .option(ChannelOption.SO_KEEPALIVE, false)
		     .childOption(ChannelOption.TCP_NODELAY, true)
		     .childOption(ChannelOption.SO_SNDBUF, 65535)
		     .childOption(ChannelOption.SO_RCVBUF, 65535)
		     .localAddress(new InetSocketAddress(port))
		     .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                         .addLast(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME,
                             new HandshakeHandler(TlsMode.PERMISSIVE))
                         .addLast(defaultEventExecutorGroup,
                             new NettyEncoder(),
                             new NettyDecoder(),
                             new IdleStateHandler(0, 0, 120),
                             new NettyConnectManageHandler(),
                             new NettyServerHandler()
                         );
                 }
             });
			
			
			
			
			
			// 服务器绑定端口监听
			ChannelFuture f = b.bind(port).sync();
			// 监听服务器关闭监听
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
		}
	}
	
	class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
        	processMessageReceived(ctx, msg);
        }
    }
	
	   public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
	        final RemotingCommand cmd = msg;
	        if (cmd != null) {
	            switch (cmd.getType()) {
	                case REQUEST_COMMAND:
	                    log.info("处理请求命令, ctx:{}, msg:{}", ctx, msg);
	                    break;
	                case RESPONSE_COMMAND:
	                    log.info("处理请求命令, ctx:{}, msg:{}", ctx, msg);
	                    break;
	                default:
	                    break;
	            }
	        }
	    }
	   
	
	
	 class HandshakeHandler extends SimpleChannelInboundHandler<ByteBuf> {

	        private final TlsMode tlsMode;

	        private static final byte HANDSHAKE_MAGIC_CODE = 0x16;
	        
	        protected SslContext sslContext = null;


	        HandshakeHandler(TlsMode tlsMode) {
	            this.tlsMode = tlsMode;
	            try {
					sslContext = TlsHelper.buildSslContext(false);
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }

	        @Override
	        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

	            // mark the current position so that we can peek the first byte to determine if the content is starting with
	            // TLS handshake
	            msg.markReaderIndex();

	            byte b = msg.getByte(0);

	            if (b == HANDSHAKE_MAGIC_CODE) {
	                switch (tlsMode) {
	                    case DISABLED:
	                        ctx.close();
	                        log.warn("Clients intend to establish a SSL connection while this server is running in SSL disabled mode");
	                        break;
	                    case PERMISSIVE:
	                    case ENFORCING:
	                        if (null != sslContext) {
	                            ctx.pipeline()
	                                .addAfter(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME, TLS_HANDLER_NAME, sslContext.newHandler(ctx.channel().alloc()))
	                                .addAfter(defaultEventExecutorGroup, TLS_HANDLER_NAME, FILE_REGION_ENCODER_NAME, new FileRegionEncoder());
	                            log.info("Handlers prepended to channel pipeline to establish SSL connection");
	                        } else {
	                            ctx.close();
	                            log.error("Trying to establish a SSL connection but sslContext is null");
	                        }
	                        break;

	                    default:
	                        log.warn("Unknown TLS mode");
	                        break;
	                }
	            } else if (tlsMode == TlsMode.ENFORCING) {
	                ctx.close();
	                log.warn("Clients intend to establish an insecure connection while this server is running in SSL enforcing mode");
	            }

	            // reset the reader index so that handshake negotiation may proceed as normal.
	            msg.resetReaderIndex();

	            try {
	                // Remove this handler
	                ctx.pipeline().remove(this);
	            } catch (Exception e) {
	                log.error("Error while removing HandshakeHandler", e);
	            }

	            // Hand over this message to the next .
	            ctx.fireChannelRead(msg.retain());
	        }
	    }
	 
	 class NettyConnectManageHandler extends ChannelDuplexHandler {
	        @Override
	        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	            log.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
	            super.channelRegistered(ctx);
	        }

	        @Override
	        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	            log.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
	            super.channelUnregistered(ctx);
	        }

	        @Override
	        public void channelActive(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	            log.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
	            super.channelActive(ctx);

	            if (NettyRemotingServer.this.channelEventListener != null) {
	                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress, ctx.channel()));
	            }
	        }

	        @Override
	        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	            log.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
	            super.channelInactive(ctx);

	            if (NettyRemotingServer.this.channelEventListener != null) {
	                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
	            }
	        }

	        @Override
	        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	            if (evt instanceof IdleStateEvent) {
	                IdleStateEvent event = (IdleStateEvent) evt;
	                if (event.state().equals(IdleState.ALL_IDLE)) {
	                    final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	                    log.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
	                    RemotingUtil.closeChannel(ctx.channel());
	                    if (NettyRemotingServer.this.channelEventListener != null) {
	                        NettyRemotingServer.this
	                            .putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
	                    }
	                }
	            }

	            ctx.fireUserEventTriggered(evt);
	        }

	        @Override
	        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
	            log.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
	            log.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

	            if (NettyRemotingServer.this.channelEventListener != null) {
	                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
	            }

	            RemotingUtil.closeChannel(ctx.channel());
	        }
	    }

	@Override
	public ChannelEventListener getChannelEventListener() {
		return null;
	}

	@Override
	public RPCHook getRPCHook() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecutorService getCallbackExecutor() {
		// TODO Auto-generated method stub
		return null;
	}
	 
}
