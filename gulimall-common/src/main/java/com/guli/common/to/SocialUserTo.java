package com.guli.common.to;

import lombok.Data;

@Data
public class SocialUserTo {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;
    private long created_at;
}
