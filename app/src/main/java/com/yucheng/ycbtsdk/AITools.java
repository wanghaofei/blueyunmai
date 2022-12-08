//package com.yucheng.ycbtsdk;
//
//
//import com.yucheng.ycbtsdk.Utils.AIPraseDataUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AITools {
//    private static AITools aiTools;
//    private List<Integer> hearts = new ArrayList<>();
//    private int hrv;
//
//    private AITools() {
//    }
//
//    public synchronized static AITools getInstance() {
//        if (aiTools == null)
//            aiTools = new AITools();
//        return aiTools;
//    }
//
//    static {
//        System.loadLibrary("new_native_lib");
//    }
//
//    public native int initHeart(int a, boolean b);
//
//    public native int makeValue(int a);
//
//    public native float getRri();
//
//    public native float getHrv();
//
//    public void Init() {
//        initHeart(250, false);
//        hearts.clear();
//        AIPraseDataUtil.vpp_array.clear();
//        AIPraseDataUtil.ecg_dataCnt = 1;
//        AIPraseDataUtil.max_val = -500;
//        AIPraseDataUtil.min_val = 500;
//    }
//
//    public int getHeart() {
//        if (hearts.size() == 0)
//            return 0;
//        return hearts.get(hearts.size() / 2);
//    }
//
//    public int getHRV() {
//        return hrv;
//    }
//
//    public List<Integer> ecgRealWaveFiltering(byte[] databytes) {
//        int offset = 0;
//        List<Integer> datas = new ArrayList<>();
//        int tIndex = 0;
//        while (offset + 2 < databytes.length) {
//            AIPraseDataUtil.praseData(databytes[offset] & 0xff, databytes[offset + 1] & 0xff, databytes[offset + 2] & 0xff, datas);
//            offset += 3;
//            int rri = (int) getRri();
//            int hrv = (int) getHrv();
//            if (rri != 0) {
//                hearts.add(60 * 1000 / rri);
//            }
//            if (hrv != 0) {
//                this.hrv = hrv;
//            }
//        }
//        return datas;
//    }
//
//    /*
//     * 处理接收完后的数据
//     * */
//    public List<Integer> getResult(List<Integer> data) {
//        return AIPraseDataUtil.aicheck(data);
//    }
//
//}
