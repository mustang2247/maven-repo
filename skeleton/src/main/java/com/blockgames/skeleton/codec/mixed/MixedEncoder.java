package com.blockgames.skeleton.codec.mixed;

import com.blockgames.skeleton.codec.CodecName;
import com.blockgames.skeleton.codec.json.JsonEncoder;
import com.blockgames.skeleton.codec.protobuf.ProtobufEncoder;
import com.blockgames.skeleton.codec.strings.StringsEncoder;
import com.blockgames.skeleton.dispatch.NetMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;

/**
 * @author mustangkong
 */
public class MixedEncoder extends MessageToByteEncoder<NetMessage> {
    private AttributeKey<Integer> key = AttributeKey.valueOf("CodecName");
    private ProtobufEncoder protobufEncoder = new ProtobufEncoder();
    private JsonEncoder jsonEncoder = new JsonEncoder();
    private StringsEncoder stringsEncoder = new StringsEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, NetMessage msg,
                          ByteBuf out) throws Exception {

        Integer codecValue = ctx.attr(key).get();
        out.writeInt(codecValue);

        if (codecValue == CodecName.PROTOBUF.getValue()) {
            protobufEncoder.encode(ctx, msg, out);
        } else if (codecValue == CodecName.JSON.getValue()) {
            jsonEncoder.encode(ctx, msg, out);
        } else if (codecValue == CodecName.STRINGS.getValue()) {
            stringsEncoder.encode(ctx, msg, out);
        } else {
            throw new RuntimeException("CodecName value " + codecValue + " not supported");
        }
    }

}
