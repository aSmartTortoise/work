package com.voyah.vcos.ttsservices.mem;
/**
 * @author:lcy
 * @data:2024/1/30
 **/
public class ByteArr {

    public static final int LEN_PER = 1024;

    private static byte[] emptyByte;

    private ByteArrObject[] byteArrObject;
    //当前使用到的索引
    private int index;

    private int amount;

    public ByteArr(int perLen, int amount) {
        init(perLen, amount);
    }

    private void init(int perLen, int amount) {
        this.amount = amount;
        //reset使用
        emptyByte = new byte[perLen];
        index = 0;
        byteArrObject = new ByteArrObject[amount];
        for (int i = 0; i < amount; i++) {
            byteArrObject[i] = new ByteArrObject(amount);
        }
    }

    public ByteArrObject getByArrObj() {
        ByteArrObject object = byteArrObject[index];
        index++;
        index %= amount;
        return object;
    }


    public static class ByteArrObject {

        public byte[] bytes;
        public int len;

        public ByteArrObject(int amount) {
            bytes = new byte[amount];
        }

        public void reset() {
            len = 0;
            System.arraycopy(emptyByte, 0, bytes, 0, emptyByte.length);
        }
    }
}
