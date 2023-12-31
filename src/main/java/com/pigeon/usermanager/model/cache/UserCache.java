package com.pigeon.usermanager.model.cache;

import com.pigeon.usermanager.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "user")
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {

    @Id
    private Long id;

    private String email;

    private String login;

    private String name;

    private UserStatus status;
}
