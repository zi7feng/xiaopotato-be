package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName Email
 */
@TableName(value ="Email")
@Data
public class Email implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long emailId;

    /**
     * 
     */
    private Long userId;

    /**
     * From user
     */
    private String fromUser;

    /**
     * To user
     */
    private String toUser;

    /**
     * Subject of E-mail
     */
    private String subject;

    /**
     * Content of E-mail
     */
    private String content;

    /**
     * Timestamp when the follow action was created
     */
    private Date createTime;

    /**
     * E-mail status: o not_send, 2 sent, 3 send_failed, 4 deleted...
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Email other = (Email) that;
        return (this.getEmailId() == null ? other.getEmailId() == null : this.getEmailId().equals(other.getEmailId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getFromUser() == null ? other.getFromUser() == null : this.getFromUser().equals(other.getFromUser()))
            && (this.getToUser() == null ? other.getToUser() == null : this.getToUser().equals(other.getToUser()))
            && (this.getSubject() == null ? other.getSubject() == null : this.getSubject().equals(other.getSubject()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEmailId() == null) ? 0 : getEmailId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getFromUser() == null) ? 0 : getFromUser().hashCode());
        result = prime * result + ((getToUser() == null) ? 0 : getToUser().hashCode());
        result = prime * result + ((getSubject() == null) ? 0 : getSubject().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", emailId=").append(emailId);
        sb.append(", userId=").append(userId);
        sb.append(", fromUser=").append(fromUser);
        sb.append(", toUser=").append(toUser);
        sb.append(", subject=").append(subject);
        sb.append(", content=").append(content);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}