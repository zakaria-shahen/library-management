package com.example.librarymanagement.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@Setter
@RedisHash(timeToLive = 216000) // 1 hour
public class BlockedTokenModel {
    @Id
    private String token;
}
