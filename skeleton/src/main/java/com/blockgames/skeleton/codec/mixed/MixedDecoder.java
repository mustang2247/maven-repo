package com.blockgames.skeleton.codec.mixed;

import com.blockgames.skeleton.codec.CodecName;
import com.blockgames.skeleton.codec.json.JsonDecoder;
import com.blockgames.skeleton.codec.protobuf.ProtobufDecoder;
import com.blockgames.skeleton.codec.strings.StringsDecoder;
import com.blockgames.skeleton.dispatch.CommandRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.util.List;

/**
 * @author mustangkong
 */
public class MixedDecoder extends ByteToMessageDecoder {

    private AttributeKey<Integer> key = AttributeKey.valueOf("CodecName");
    private boolean saveCodecName = false;
    private ProtobufDecoder protobufDecoder = null;
    private JsonDecoder jsonDecoder = null;
    private StringsDecoder stringsDecoder = null;

    public MixedDecoder(CommandRegistry registry) {
        this.protobufDecoder = new ProtobufDecoder(registry);
        this.jsonDecoder = new JsonDecoder(registry);
        this.stringsDecoder = new StringsDecoder();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {

        int codecValue = in.readInt();
        if (!saveCodecName) {
            ctx.attr(key).set(codecValue);
            saveCodecName = true;
        }

        if (codecValue == CodecName.PROTOBUF.getValue()) {
            protobufDecoder.decode(ctx, in, out);
        } else if (codecValue == CodecName.JSON.getValue()) {
            jsonDecoder.decode(ctx, in, out);
        } else if (codecValue == CodecName.STRINGS.getValue()) {
            stringsDecoder.decode(ctx, in, out);
        } else {
            throw new RuntimeException("CodecName value " + codecValue + " not supported");
        }
    }

    @Override
    public void decodeLast(ChannelHandlerContext ctx, ByteBuf msg,
                           List<Object> out) throws Exception {
        // do nothing
    }
}
