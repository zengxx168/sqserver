/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.bootstrap;

import io.game.bootstrap.netty.NettyRequestProcessor;
import io.game.bootstrap.sessions.SessionsManager;

/**
 * @author Administrator
 * @version $Id: RemotingServer.java 2021年6月15日 下午2:13:02 $
 */
public interface RemotingServer extends RemotingService {

    public void setSessionManager(SessionsManager sessionManager);

    /**
     * 注册服务
     *
     * @param requestCode
     * @param processor
     */
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor);
}
