package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int i = in.readableBytes();
		if (i == 0) {
			return;
		}

		int id = PacketBufUtils.readSignedVarInt(in);
		var clazz = Packet.ID_TO_CLASS.get(id);

		if (clazz == null) {
			throw new IllegalStateException("Invalid packet id " + id);
		}

		Packet packet = clazz.getConstructor().newInstance();
		packet.read(in);
		out.add(packet);
	}
}
