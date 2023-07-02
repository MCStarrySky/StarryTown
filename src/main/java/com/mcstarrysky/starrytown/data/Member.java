package com.mcstarrysky.starrytown.data;

import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author xiaomu
 * @since 2022/8/29 09:26
 */
@Data
@Builder
public class Member {

    private UUID player;
    private long timestamp;

    public String getTime() {
        return new SimpleDateFormat().format(new Date(timestamp));
    }
}
