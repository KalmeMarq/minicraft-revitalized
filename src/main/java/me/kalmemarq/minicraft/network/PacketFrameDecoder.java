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
