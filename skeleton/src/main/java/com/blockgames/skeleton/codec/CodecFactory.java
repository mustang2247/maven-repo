package com.blockgames.skeleton.codec;

import com.blockgames.skeleton.codec.json.JsonDecoder;
import com.blockgames.skeleton.codec.json.JsonEncoder;
import com.blockgames.skeleton.codec.mixed.MixedDecoder;
import com.blockgames.skeleton.codec.mixed.MixedEncoder;
import com.blockgames.skeleton.codec.protobuf.ProtobufDecoder;
import com.blockgames.skeleton.codec.protobuf.ProtobufEncoder;
import com.blockgames.skeleton.codec.strings.StringsDecoder;
import com.blockgames.skeleton.codec.strings.StringsEncoder;
import com.blockgames.skeleton.dispatch.CommandRegistry;
import io.netty.channel.ChannelHandler;

/**
 * @author mustangkong
 */
public class CodecFactory {
    public static ChannelHandler getDecoder(CodecName codecName, CommandRegistry registry) {
        switch (codecName) {
            case MIXED: {
                return new MixedDecoder(registry);
            }
            case PROTOBUF: {
                return new ProtobufDecoder(registry);
            }
            case JSON: {
                return new JsonDecoder(registry);
            }
            case STRINGS: {
                return new StringsDecoder();
            }
            default:
                throw new RuntimeException("Not support decoder " + codecName);
        }
    }

    public static ChannelHandler getEncoder(CodecName codecName) {
        switch (codecName) {
            case MIXED: {
                return new MixedEncoder();
            }
            case PROTOBUF: {
                return new ProtobufEncoder();
            }
            case JSON: {
                return new JsonEncoder();
            }
            case STRINGS: {
                return new StringsEncoder();
            }
            default:
                throw new RuntimeException("Not support encoder " + codecName);
        }
    }
}
