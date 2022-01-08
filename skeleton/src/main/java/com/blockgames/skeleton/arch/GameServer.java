package com.blockgames.skeleton.arch;

import com.blockgames.skeleton.base.Finalizer;
import com.blockgames.skeleton.base.Initializer;
import com.blockgames.skeleton.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;
import sun.misc.SignalHandler;

//@formatter:off

/**
 * GameServer框架
 */
@Slf4j
public class GameServer {
    // 通过linux信号退出
    private static class SafeExitHandler implements SignalHandler {

        //private static final Logger log = LoggerFactory.getLogger(SafeExitHandler.class);
        private GameServer gameServer;

        public SafeExitHandler(GameServer gs) {
            gameServer = gs;
        }

        @Override
        public void handle(Signal arg0) {
            log.info("GameServer-{} receive TERM signal, will stop!", gameServer.getName());
            gameServer.stopServer();
        }
    }

    // 这是在jvm退出时会调用的hook
    // 如果不是正常途径退出，希望给服务器一个清理的机会
    // 但是如果发送kill -9，那就没有机会了
    @Slf4j
    private static class CleanupThread extends Thread {
        //private static final Logger log = LoggerFactory.getLogger( CleanupThread.class );
        private GameServer gameServer;

        public CleanupThread(GameServer gs) {
            gameServer = gs;
        }

        @Override
        public void run() {
            if (gameServer.getServerState() != ServerState.DESTROY) {
                log.info("GameServer-{} abnormal exit, enter CleanupThread, will stop!", gameServer.getName());
                gameServer.stopServer();
            }
        }
    }

    public enum ServerState {
        BORN, INIT, RUN, SUSPEND, DESTROY
    }

    //private static final Logger log = LoggerFactory.getLogger(GameServer.class);

    /**
     * 服务标识
     */
    protected String name;
    /**
     * 服务配置
     */
    protected ServerConfig config;
    /**
     * 服务状态
     */
    protected volatile ServerState serverState = ServerState.BORN;
    /**
     * 初始化接口
     */
    protected Initializer initializer = null;
    /**
     * 终止接口
     */
    protected Finalizer finalizer = null;
    /**
     * netty管道生成器
     */
    protected ChannelInitializer<SocketChannel> channelInitializer = null;
    /**
     * netty 服务框架
     */
    protected ServerBootstrap svcBootstrap = null;

    protected EventLoopGroup svcBossGroup = null;

    protected EventLoopGroup svcWorkerGroup = null;

    protected ChannelFuture svcChannelFuture = null;

    public GameServer(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setServerState(ServerState state) {
        this.serverState = state;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void pauseServer() {
        setServerState(ServerState.SUSPEND);
    }

    public void resumeServer() {
        setServerState(ServerState.RUN);
    }

    public GameServer setInitializer(Initializer init) {
        initializer = init;
        return this;
    }

    public GameServer setFinalizer(Finalizer f) {
        finalizer = f;
        return this;
    }

    public GameServer setChannelInitializer(ChannelInitializer<SocketChannel> ci) {
        channelInitializer = ci;
        return this;
    }

    public void initServer(String configFilePath) {

        config = new ServerConfig();
        config.init(configFilePath);

        initServer(config);
    }

    public void initServer(ServerConfig config) {

        this.config = config;

        if (initializer != null) {
            initializer.init();
        }

        setServerState(ServerState.INIT);
    }

    public void startServer() {
        startServer(true);
    }

    public void startServer(boolean installSignal) {

        if (this.channelInitializer == null) {
            log.error("GameServer-{} channelInitializer is NULL, start fail!", name);
            throw new RuntimeException();
        }

        if (this.getServerState() != ServerState.INIT) {
            log.error("GameServer-{} is not init, start fail!", name);
            throw new RuntimeException();
        }

        Runtime.getRuntime().addShutdownHook(new CleanupThread(this));

        if (installSignal) {
            Signal.handle(new Signal("TERM"), new SafeExitHandler(this));
        }

        // TODO
        // if( PlatformDependent.isWindows() ) {
        if (true) {
            svcBossGroup = new NioEventLoopGroup(config.ACCEPT_THREAD_NUM);
            svcWorkerGroup = new NioEventLoopGroup(config.IO_THREAD_NUM);
            svcBootstrap = new ServerBootstrap();
            svcBootstrap
                    .group(svcBossGroup, svcWorkerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, config.ACCEPT_BACKLOG_NUM)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

        }

//		else { // 必须是linux
//
//			svcBossGroup = new EpollEventLoopGroup(
//					config.ACCEPT_THREAD_NUM );
//			svcWorkerGroup = new EpollEventLoopGroup( config.IO_THREAD_NUM );
//			svcBootstrap = new ServerBootstrap();
//			svcBootstrap
//				.group( svcBossGroup, svcWorkerGroup )
//				.channel( EpollServerSocketChannel.class )
//				.childHandler( channelInitializer )
//				.option( ChannelOption.SO_BACKLOG, config.ACCEPT_BACKLOG_NUM )
//				.option( ChannelOption.TCP_NODELAY, true )
//				.childOption( ChannelOption.SO_KEEPALIVE, true )
//				.childOption( EpollChannelOption.TCP_KEEPIDLE, 5000 )
//				.childOption( EpollChannelOption.TCP_KEEPINTVL, 5000 )
//				.childOption( EpollChannelOption.TCP_KEEPCNT, 2 );
//		}

        try {
            svcChannelFuture = svcBootstrap.bind(config.LISTEN_PORT).sync();

            setServerState(ServerState.RUN);

            log.info("GameServer-{} start, listen on {} for service",
                    name,
                    svcChannelFuture.channel().localAddress());
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void stopServer() {

        if (getServerState() == ServerState.DESTROY) {
            return;
        }

        setServerState(ServerState.DESTROY);

        log.info("GameServer-{} stopping...", name);

        try {
            if (finalizer != null) {
                finalizer.end();
            }
            svcBossGroup.shutdownGracefully().sync();
            svcWorkerGroup.shutdownGracefully().sync();

            log.info("GameServer-{} stopped!", name);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
