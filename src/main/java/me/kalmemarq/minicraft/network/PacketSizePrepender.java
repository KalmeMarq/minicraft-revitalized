/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

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
