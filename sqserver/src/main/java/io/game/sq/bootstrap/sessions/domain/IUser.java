/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.sq.bootstrap.sessions.domain;

import io.game.sq.bootstrap.protocols.Command;

/**
 * @author Administrator
 * @version $Id: IUser.java 2021年7月1日 下午5:47:07 $
 */
public interface IUser {

    long getId();

    boolean isConnected();

    long getServerId();

    String getIp();

    int getMapId();

    /**
     * 当前线程发送数据
     *
     * @param command
     */
    void sendMessage(Command command);
}
