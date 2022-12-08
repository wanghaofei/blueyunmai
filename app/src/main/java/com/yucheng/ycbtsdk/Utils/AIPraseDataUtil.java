//package com.yucheng.ycbtsdk.Utils;
//
//import com.yucheng.ycbtsdk.AITools;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author zengchong
// * @date 2020/7/2
// * @desc one word for this class
// */
//public class AIPraseDataUtil {
//    public static int ecg_dataCnt;
//    public static int max_val, min_val;
//    public static List<Integer> vpp_array = new ArrayList<>();
//
//    public static void praseData(int var1, int var2, int var3, List<Integer> datas) {
//        int tData = (var1 & 0xff) + (var2 << 8) + (var3 << 16);
//        if ((var3 & 0x80) != 0) {
//            tData |= 0xff000000;
//        }
//        int Ecg_val = AITools.getInstance().makeValue(tData);
//        Ecg_val = (int) (Ecg_val * 0.007);
//        if (Ecg_val > 400) {
//            Ecg_val = 400;
//        }
//        if (Ecg_val < -400) {
//            Ecg_val = -400;
//        }
//        datas.add(Ecg_val);
//        if (Ecg_val > max_val) {
//            max_val = Ecg_val;
//        } else if (Ecg_val < min_val) {
//            min_val = Ecg_val;
//        }
//        ecg_dataCnt++;
//        if (ecg_dataCnt % 400 == 0) {
//            int vpp = max_val - min_val;
//            if (vpp > 0) {
//                vpp_array.add(vpp);
//            }
//            min_val = 500;
//            max_val = -500;
//        }
//    }
//
//    // 整合 ECG 数据
//    public static List<Integer> aicheck(List<Integer> blist) {
//        //add by shl 2019-11-08
//        List<Integer> m_blist = new ArrayList<>();
//        Collections.sort(vpp_array);
//        float radio = 0.0f;
//        int sum = 0;
//        int cnt = 0;
//        if (vpp_array.size() > 11) {
//            for (int i = 5; i < vpp_array.size() - 10; i++) {
//                sum += vpp_array.get(i);
//                cnt++;
//            }
//            radio = sum * 1.0f / cnt;
//        }
//        if (radio < 120) {
//            radio = 120 / radio;
//        } else {
//            radio = 1;
//        }
//        int offset = 0;
//        int len_of_list = blist.size();
//
//        while (offset < len_of_list) {
//            int data = (int) (radio * blist.get(offset));
//            if (data > 400) {
//                data = 400;
//            } else if (data < -400) {
//                data = -400;
//            }
//            m_blist.add(data);
//            offset++;
//        }
//        return m_blist;
//    }
//
//
//}
