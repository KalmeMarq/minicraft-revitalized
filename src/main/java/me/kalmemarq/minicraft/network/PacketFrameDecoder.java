package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PacketFrameDecoder extends ByteToMessageDecoder {
	private final ByteBuf reusableBuf = Unpooled.directBuffer(3);

	@Override
	protected void handlerRemoved0(ChannelHandlerContext context) {
		this.reusableBuf.release();
	}

	private static boolean shouldSplit(ByteBuf source, ByteBuf sizeBuf) {
		for (int i = 0; i < 3; ++i) {
			if (!source.isReadable()) {
				return false;
			}
			byte b = source.readByte();
			sizeBuf.writeByte(b);
			if ((b & 0x80) == 128) continue;
			return true;
		}
		throw new CorruptedFrameException("Length wider than 21-bit");
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		in.markReaderIndex();
		this.reusableBuf.clear();
		if (!shouldSplit(in, this.reusableBuf)) {
			in.resetReaderIndex();
			return;
		}
		int length = PacketBufUtils.readSignedVarInt(this.reusableBuf);
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}
		out.add(in.readBytes(length));
	}
}
