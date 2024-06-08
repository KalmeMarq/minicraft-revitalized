package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketSizePrepender extends MessageToByteEncoder<ByteBuf> {
	// io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int bodyLen = msg.readableBytes();
		int headerLen = PacketBufUtils.getVarIntLength(bodyLen);
		if (headerLen > 3) {
			throw new EncoderException("Packet is too large: size " + bodyLen + " is over 8");
		}
		out.ensureWritable(headerLen + bodyLen);
		PacketBufUtils.writeSignedVarInt(bodyLen, out);
		out.writeBytes(msg, msg.readerIndex(), bodyLen);
	}
}
