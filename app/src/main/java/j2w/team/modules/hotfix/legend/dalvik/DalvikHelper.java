package j2w.team.modules.hotfix.legend.dalvik;

/**
 * @author Lody
 * @version 1.0
 */
public class DalvikHelper {

    public static int dvmComputeJniArgInfo(String shorty) {
        int returnType, jniArgInfo;

        int hints;
        /* The first shorty character is the return type. */
        switch (shorty.charAt(0)) {
            case 'V':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_VOID;
                break;
            case 'F':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_FLOAT;
                break;
            case 'D':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_DOUBLE;
                break;
            case 'J':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_S8;
                break;
            case 'Z':
            case 'B':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_S1;
                break;
            case 'C':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_U2;
                break;
            case 'S':
                returnType = DalvikConstants.DALVIK_JNI_RETURN_S2;
                break;
            default:
                returnType = DalvikConstants.DALVIK_JNI_RETURN_S4;
                break;
        }//end switch

        jniArgInfo = returnType << DalvikConstants.DALVIK_JNI_RETURN_SHIFT;
        hints = dvmPlatformInvokeHints(shorty);

        if ((hints & DalvikConstants.DALVIK_JNI_NO_ARG_INFO) != 0) {
            jniArgInfo |= DalvikConstants.DALVIK_JNI_NO_ARG_INFO;
        } else {
            jniArgInfo |= hints;
        }

        return jniArgInfo;
    }


    public static int dvmPlatformInvokeHints(String shorty) {
        int padFlags, jniHints;
        char sigByte;
        int stackOffset, padMask;

        stackOffset = padFlags = 0;
        padMask = 0x00000001;

        /* Skip past the return type */
        int index = 1;
        while (index < shorty.length()) {
            sigByte = shorty.charAt(index);
            index++;
            if (sigByte == 'D' || sigByte == 'J') {
                if ((stackOffset & 1) != 0) {
                    padFlags |= padMask;
                    stackOffset++;
                    padMask <<= 1;
                }
                stackOffset += 2;
                padMask <<= 2;
            } else {
                stackOffset++;
                padMask <<= 1;
            }
        }
        jniHints = 0;
        if (stackOffset > DalvikConstants.DALVIK_JNI_COUNT_SHIFT) {
		/* too big for "fast" version */
            jniHints = DalvikConstants.DALVIK_JNI_NO_ARG_INFO;
        } else {
            stackOffset -= 2;           // r2/r3 holds first two items
            if (stackOffset < 0)
                stackOffset = 0;
            jniHints |= ((stackOffset + 1) / 2) << DalvikConstants.DALVIK_JNI_COUNT_SHIFT;
            jniHints |= padFlags;
        }

        return jniHints;
    }

    public static int dvmCalcMethodArgsSize(String shorty) {
        int count = 0;
        /* Skip the return type. */
        int index = 1;
        char currentChar;
        while (index < shorty.length()) {
            currentChar = shorty.charAt(index);
            index++;
            switch (currentChar) {
                case 'D':
                case 'J':{
                    count += 2;
                    break;
                }
                default:
                    count++;
                    break;
            }//end switch

        }//end while
        return count;

    }
}
