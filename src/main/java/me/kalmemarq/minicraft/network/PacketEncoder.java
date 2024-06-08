package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		int id = Packet.CLASS_TO_ID.get(msg.getClass());

		if (!Packet.CLASS_TO_ID.containsKey(msg.getClass())) {
			throw new IllegalStateException("Invalid packet " + msg.getClass().getSimpleName());
		}

		PacketBufUtils.writeSignedVarInt(id, out);
		msg.write(out);
	}
}
