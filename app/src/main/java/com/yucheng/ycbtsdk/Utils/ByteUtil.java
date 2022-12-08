package com.yucheng.ycbtsdk.Utils;

/**
 * @author StevenLiu
 * @date 2020/1/13
 * @desc one word for this class
 */
public class ByteUtil {

    public static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        if (!isEmpty(bytes)) {
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
        }

        return sb.toString();
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }


    public static byte[] fromShort(short n) {
        return new byte[]{
                (byte) n, (byte) (n >>> 8)
        };
    }

    public static byte[] fromInt(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
    }

    public static byte[] fromLong(long n) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
    }

    public static int ubyteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public static int crc16_compute(byte[] p_data, int p_size) {

        short crc = (short) 0xffff;
        for (int i = 0; i < p_size; ++i) {

//            Log.e("qob",  "crc " + String.format("%04x", crc >> 8) + " " + String.format("%04x", crc << 8));

            crc = (short) ((crc >> 8 & 0x00ff) | (crc << 8 & 0xff00));

//            Log.e("qob", "crc1 " + String.format("%08x", crc));

            crc ^= (p_data[i] & 0x00ff);

//            Log.e("qob", "crc2 " + String.format("%08x", crc));

            short tTemp = (byte) ((crc & 0xff) >> 4);
            crc ^= tTemp;

//            Log.e("qob", "crc3 " + String.format("%04x", crc) + " " + String.format("%04x", tTemp));

            crc ^= (crc << 8) << 4;

//            Log.e("qob", "crc4 " + String.format("%04x", crc));

            crc ^= ((crc & 0xFF) << 4) << 1;

//            Log.e("qob", "crc5 " + String.format("%04x", crc));
        }

        return crc & 0xffff;

    }

    public static byte[] stringToByte(String title, String content, int count, int type) {
        try {
            if (count > 0 && title != null && title.length() > 0 && content != null && content.length() > 0) {
                String data = content;
                if (data.length() > count) {
                    data = getData(content, count);
                }
                byte[] smsg = {(byte) type};
                byte[] aa = byteMerger(smsg, title.getBytes("utf-8"));
                byte[] aa0 = {0x00};
                byte[] aa1 = byteMerger(aa, aa0);
                byte[] senda = byteMerger(aa1, data.getBytes("utf-8"));
                return byteMerger(senda, aa0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private static String getData(String msg, int count) {
        int n = 0;
        try {
            byte[] bytes = msg.substring(0, count).getBytes("utf-8");
            boolean is_flag = true;
            while (is_flag) {
                n++;
                if (bytes.length < (count - 1) * 3 && count + n < msg.length()) {
                    bytes = msg.substring(0, count + n).getBytes("utf-8");
                } else {
                    is_flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg.substring(0, count + n - 2);
    }


}
