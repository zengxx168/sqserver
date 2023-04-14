package io.game.sq.core.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * 账号表
 * </p>
 *
 * @author zengxx
 * @version $Id: User.java 2023-02-01 $
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user", autoResultMap = true)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 应用ID
     */
    @TableField("appid")
    private int appid;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 服务器ID
     */
    @TableField("server_id")
    private long serverId;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 身份证
     */
    @TableField("id_card")
    private String idCard;

    /**
     * IP
     */
    @TableField("ip")
    private String ip;

    /**
     * 客户端类型
     */
    @TableField("oss")
    private String oss;

    /**
     * 设备号
     */
    @TableField("cid")
    private String cid;

    /**
     * 0 正常，1锁定 2 停用
     */
    @TableField("status")
    private int status;

    /**
     * 创建日期
     */
    @TableField("create_date")
    private long createDate;

    public int age() {
        if (!StringUtils.isEmpty(idCard)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            try {
                LocalDate birthDate = LocalDate.parse(idCard.substring(6, 14), formatter);
                return Period.between(birthDate, LocalDate.now()).getYears();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}