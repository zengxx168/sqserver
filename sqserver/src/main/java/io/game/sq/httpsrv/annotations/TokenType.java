package io.game.sq.httpsrv.annotations;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 功能说明：是否需求会话检查
 * </pre>
 *
 * @author Admin
 * @version 1.0
 */
public enum TokenType {

    YES, NO, DEFAULT;

    public boolean isCheck(String token) {
        if (YES == this) {
            return true;
        }
        if (DEFAULT == this && !StringUtils.isEmpty(token)) {
            return true;
        }
        return false;
    }

}

