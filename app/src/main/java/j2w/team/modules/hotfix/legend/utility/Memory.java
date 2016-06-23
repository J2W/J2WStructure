package j2w.team.modules.hotfix.legend.utility;

import android.util.Log;


public class Memory {

    public static void copy(long dest, long src, int size) {
        LegendNative.memcpy(dest,src,size);
    }

    public static void write(byte[] data, long dest){
        Log.d("#######","Write Memory to 0x" + Long.toHexString(dest));
        LegendNative.memput(dest,data);
    }

    public static void write(long dest, byte[] data){
        Log.d("#######","Write Memory to 0x" + Long.toHexString(dest));
        LegendNative.memput(dest,data);
    }

    public static byte[] read(long address, int size){
        Log.d("#######","Read Memory to 0x" + Long.toHexString(address));
        return LegendNative.memget(address,size);
    }

    public static void unlock(long address, int size){
        LegendNative.munprotect(address, size);
    }

    public static long alloc(int size){
        Log.d("#######","Malloc memory, size : " + size);
        return LegendNative.malloc(size);
    }

    public static void free(long pointer, int length) {
        Log.d("######","Free memory to 0x" + Long.toHexString(pointer));
        LegendNative.free(pointer, length);
    }

}
