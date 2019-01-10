package com.example.model;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Collection;

@Getter
@Setter
@ToString
public class CustomUser extends User {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // 유저의 정보를 더 추가하고 싶다면 이곳과, 아래의 생성자 파라미터를 조절해야 한다.
    private String user_name;

    public CustomUser(String username, String password
            , boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked
            , Collection authorities
            , String user_name) {
        super(username, password
                , enabled, accountNonExpired, credentialsNonExpired, accountNonLocked
                , authorities);
        
        this.user_name = user_name;
    }
}