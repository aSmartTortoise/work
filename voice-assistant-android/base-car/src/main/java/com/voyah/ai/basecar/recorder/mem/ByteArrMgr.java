package com.voyah.ai.basecar.recorder.mem;

public class ByteArrMgr {

    private static byte[] emptyArr;

    private ByteArrObj[] bAObjArr;

    private int amount;

    private int index;

    public ByteArrMgr(int perLen, int amount) {
        init(perLen, amount);
    }

    private void init(int perLen, int amount) {
        this.amount = amount;
        emptyArr = new byte[perLen];
        bAObjArr = new ByteArrObj[amount];
        index = 0;
        for (int i = 0; i < amount; i++) {
            bAObjArr[i] = new ByteArrObj(perLen);
        }
    }

    public ByteArrObj getByteArrObj() {
        ByteArrObj obj = bAObjArr[index];
        index++;
        index %= amount;
        return obj;
    }


    public static class ByteArrObj {

        public int len;
        public byte[] arr;

        public ByteArrObj(int amount) {
            arr = new byte[amount];
        }

        public void reset() {
            len = 0;
            System.arraycopy(emptyArr, 0, arr, 0, emptyArr.length);
        }
    }
}
