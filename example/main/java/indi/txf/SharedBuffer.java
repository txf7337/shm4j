package indi.txf;

import indi.txt.shm.SharedMemory;

import java.io.IOException;

/**
 * Created by dabao on 2017/7/13.
 */
public class SharedBuffer extends SharedMemory {
    private final int MAX_INDEX;
    private final int WRTIE_INDEX_OFFSET;
    private final int READ_INDEX_OFFSET;
    private final int FLAG;

    public SharedBuffer(int key, int capacity, boolean readOnly, boolean createIfNotExist) throws IOException {
        super(key, capacity, readOnly, createIfNotExist);
        MAX_INDEX = capacity - 10;
        WRTIE_INDEX_OFFSET = capacity - 8;
        READ_INDEX_OFFSET = capacity - 4;
        FLAG = capacity - 9;
    }

    private int readIndex() {
        return pointer.getInt(READ_INDEX_OFFSET);
    }

    private int writeIndex() {
        return pointer.getInt(WRTIE_INDEX_OFFSET);
    }

    private byte flag() {
        return pointer.getByte(FLAG);
    }

    public int readableBytes() {
        return writeIndex() + MAX_INDEX * flag() - readIndex();
    }

    public int writableBytes() {
        return readIndex() + MAX_INDEX * (flag() ^ 1) - writeIndex();
    }

    public byte[] readBytes() {
        int readableBytes = readableBytes();
        if (readableBytes == 0) {
            return null;
        }
        int readIndex = readIndex();
        byte[] bytes;
        if (readableBytes + readIndex > MAX_INDEX) {
            bytes = new byte[readableBytes];
            int i = MAX_INDEX - readIndex;
            pointer.read(readIndex, bytes, 0, i);
            pointer.read(0, bytes, i, readableBytes - i);
            _write(FLAG, 0, 1, (byte) (flag() ^ 1));
            _write(READ_INDEX_OFFSET, readableBytes - i);
        } else {
            bytes = pointer.getByteArray(readIndex, readableBytes);
            _write(READ_INDEX_OFFSET, readIndex + readableBytes);
        }
        return bytes;
    }

    public void writeBytes(byte... bytes) {
        int writableBytes = writableBytes();
        int length = bytes.length;
        if (length > writableBytes) {
            throw new IndexOutOfBoundsException("no space to write!writableBytes:" + writableBytes);
        }
        int writeIndex = writeIndex();
        if (writeIndex + length > MAX_INDEX) {
            int i = MAX_INDEX - writeIndex;
            _write(writeIndex, 0, i, bytes);
            _write(0, i, length - i, bytes);
            _write(FLAG, 0, 1, (byte) (flag() ^ 1));
            writeIndex = length - i;
            _write(WRTIE_INDEX_OFFSET, writeIndex);
        } else {
            _write(writeIndex, 0, length, bytes);
            _write(WRTIE_INDEX_OFFSET, writeIndex + length);
        }
    }

    private void _write(int offset, int... ints) {
        pointer.write(offset, ints, 0, ints.length);
    }

    private void _write(int offset, int index, int length, byte... bytes) {
        pointer.write(offset, bytes, index, length);
    }
}
