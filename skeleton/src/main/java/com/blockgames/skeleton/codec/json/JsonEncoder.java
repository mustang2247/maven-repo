package com.blockgames.skeleton.codec.json;

import com.blockgames.skeleton.dispatch.NetMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author mustangkong
 */
public class JsonEncoder extends MessageToByteEncoder<NetMessage> {

    @Override
    public void encode(ChannelHandlerContext ctx, NetMessage msg,
                       ByteBuf out) throws Exception {
        out.writeInt(msg.getKindId());
        out.writeInt(msg.getMsgId());
        out.writeBytes(JsonUtil.encodeBytes(msg.getMessage()));
    }

}
