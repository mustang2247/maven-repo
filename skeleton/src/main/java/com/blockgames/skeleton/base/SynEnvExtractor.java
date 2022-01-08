package com.blockgames.skeleton.base;

@FunctionalInterface
public interface SynEnvExtractor {
    SynEnvBase extract( Object obj );
}
