package com.blockgames.skeleton.base;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mustangkong
 */
@Slf4j
public abstract class SynEnvTask implements Runnable {

    //private static final Logger log = LoggerFactory.getLogger( SynEnvTask.class );

    private final SynEnvBase synEnv;

    public SynEnvTask(SynEnvBase synEnv) {
        this.synEnv = synEnv;
    }

    public boolean isSynEnvProcessing() {
        return getSynEnv().isProcessing();
    }

    public void setSynEnvProcessing(boolean isProcessing) {
        getSynEnv().setProcessing(isProcessing);
    }

    public SynEnvBase getSynEnv() {
        return synEnv;
    }

    @Override
    public void run() {
        try {
            processTask();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            getSynEnv().setProcessing(false);
        }
    }

    public abstract void processTask();

}
