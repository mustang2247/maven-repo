package com.blockgames.skeleton.base;

import lombok.Data;

/**
 * @author mustangkong
 */
@Data
public class SynEnvBase {

    private final long synId;
    private boolean isProcessing;

    public SynEnvBase(long id) {
        this.synId = id;
        this.isProcessing = false;
    }

}
