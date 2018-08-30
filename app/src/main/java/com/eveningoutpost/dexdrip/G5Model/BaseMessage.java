package com.eveningoutpost.dexdrip.G5Model;

import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Services.G5CollectionService;
import com.google.gson.annotations.Expose;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// jamorham

public class BaseMessage {

    protected static final String TAG = G5CollectionService.TAG; // meh
    static final int INVALID_TIME = 0xFFFFFFFF;
    @Expose
    long postExecuteGuardTime = 50;
    @Expose
    public volatile byte[] byteSequence;
    public ByteBuffer data;


    void init(final byte opcode, final int length) {
        data = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        data.put(opcode);
        if (length == 1) {
            getByteSequence();
        } else if (length == 3) {
            appendCRC();
        }
    }

    byte[] appendCRC() {
        data.put(FastCRC16.calculate(getByteSequence(), byteSequence.length - 2));
        return getByteSequence();
    }

    boolean checkCRC(byte[] data) {
        if ((data == null) || (data.length < 3)) return false;
        final byte[] crc = FastCRC16.calculate(data, data.length - 2);
        return crc[0] == data[data.length - 2] && crc[1] == data[data.length - 1];
    }

    byte[] getByteSequence() {
        return byteSequence = data.array();
    }

    long guardTime() {
        return postExecuteGuardTime;
    }

    static int getUnsignedShort(ByteBuffer data) {
        return ((data.get() & 0xff) + ((data.get() & 0xff) << 8));
    }

    static int getUnsignedByte(ByteBuffer data) {
        return ((data.get() & 0xff));
    }

    static int getUnixTime() {
        return (int) (JoH.tsl() / 1000);
    }
}