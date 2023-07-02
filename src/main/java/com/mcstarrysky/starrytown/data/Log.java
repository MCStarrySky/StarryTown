package com.mcstarrysky.starrytown.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * StarryTown
 * data.com.mcstarrysky.starrytown.Log
 *
 * @author mical
 * @since 2023/7/2 10:11 AM
 */
@Data
@Builder
public class Log {

    private long timestamp;
    private UUID player;
    private String content;
}
