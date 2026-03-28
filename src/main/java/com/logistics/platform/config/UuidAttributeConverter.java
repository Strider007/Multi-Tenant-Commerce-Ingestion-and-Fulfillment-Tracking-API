package com.logistics.platform.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.ByteBuffer;
import java.util.UUID;

@Converter(autoApply = true)
public class UuidAttributeConverter implements AttributeConverter<UUID, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(UUID uuid) {
        if (uuid == null) return null;
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] bytes = bb.array();
        byte[] swapped = new byte[16];
        swapped[0] = bytes[6]; swapped[1] = bytes[7];
        swapped[2] = bytes[4]; swapped[3] = bytes[5];
        swapped[4] = bytes[0]; swapped[5] = bytes[1];
        swapped[6] = bytes[2]; swapped[7] = bytes[3];
        System.arraycopy(bytes, 8, swapped, 8, 8);
        return swapped;
    }

    @Override
    public UUID convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        byte[] unswapped = new byte[16];
        unswapped[0] = bytes[4]; unswapped[1] = bytes[5];
        unswapped[2] = bytes[6]; unswapped[3] = bytes[7];
        unswapped[4] = bytes[2]; unswapped[5] = bytes[3];
        unswapped[6] = bytes[0]; unswapped[7] = bytes[1];
        System.arraycopy(bytes, 8, unswapped, 8, 8);
        ByteBuffer bb = ByteBuffer.wrap(unswapped);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
