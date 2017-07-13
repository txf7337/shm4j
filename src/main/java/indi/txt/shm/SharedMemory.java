package indi.txt.shm;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SharedMemory {
    private final Logger logger = LoggerFactory.getLogger(SharedMemory.class);
    protected final int capacity;
    private final int id;
    private final int key;
    private final boolean readOnly;
    private final boolean created;
    private final long address;
    protected Pointer pointer;
    private static final Lib lib;

    interface Lib extends Library {
        int d(long address);

        int g(long key, long size, int shmFlg);

        long a(int shmId, Pointer pointer, int shmFlg);

        int c(int shmId, int cmd);
    }

    static {
        lib = Native.loadLibrary(Platform.isWindows() ? "/shm.dll" : "/shm.so", Lib.class);
    }

    public SharedMemory(int key, int capacity, boolean readOnly, boolean createIfNotExist) throws IOException {
        this.key = key;
        this.capacity = capacity;
        int shmId = lib.g(this.key, this.capacity, 0x0);
        if (shmId == -1) {
            logger.warn("shared memory already created!key:" + key);
            if (createIfNotExist) {
                shmId = lib.g(this.key, this.capacity, 0x200);
                if (shmId == -1) {
                    throw new IOException("create shared memory error!errno:" + Native.getLastError());
                } else {
                    this.created = true;
                }
            } else {
                throw new NullPointerException("shared memory not exist");
            }
        } else {
            this.created = false;
        }
        this.id = shmId;
        this.readOnly = readOnly;
        address = lib.a(this.id, null, this.readOnly ? 0x1000 : 0x0);
        if (address == -1) {
            try {
                free();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            throw new IOException("create image error!errno:" + Native.getLastError());
        }
        this.pointer = new Pointer(address);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * free image address
     */
    public void close() throws IOException {
        try {
            int result = lib.d(this.address);
            if (result == -1) {
                throw new IOException("free image address error!errno:" + Native.getLastError());
            }
        } finally {
            if (created) {
                free();
            }
        }
    }

    /**
     * free shared memory
     *
     * @throws IOException
     */
    protected void free() throws IOException {
        int result = lib.c(id, 0x0);
        if (result == -1) {
            throw new IOException("free shared memory error!errno:" + Native.getLastError());
        }
    }
}