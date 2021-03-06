package com.danyun.hades.constant;


public class ConstantString {


    /*娃娃机相关*/
    public static int Catcher_Status_Free = 0;  // 娃娃机空闲并且没有被玩家临时占用

    public static int Catcher_Status_Using = 1; // 有玩家在操作娃娃机

    public static int Catcher_Status_Own = 2;   // 玩家临时拥有娃娃机，但是没有进行游戏

    public static int Catcher_Online = 1;

    public static int Catcher_Offline = 0;



    public static String Catcher_Operation_PalyGame = "0001";

    public static String Catcher_Operation_Conti_To_South = "0002";

    public static String Catcher_Operation_Conti_To_North = "0003";

    public static String Catcher_Operation_Conti_To_West = "0004";

    public static String Catcher_Operation_Conti_To_East = "0005";

    public static String Catcher_Operation_Catch_Doll = "0006";

    public static String Catcher_Operation_Conti_To_E_S = "0008";

    public static String Catcher_Operation_Conti_To_E_N = "0009";

    public static String Catcher_Operation_Conti_To_W_S = "0010";

    public static String Catcher_Operation_Conti_To_W_N = "0011";

    public static String Catcher_Operation_To_South = "0102";

    public static String Catcher_Operation_To_North = "0103";

    public static String Catcher_Operation_To_West = "0104";

    public static String Catcher_Operation_To_East = "0105";

    public static String Catcher_Operation_To_E_S = "0108";

    public static String Catcher_Operation_To_E_N = "0109";

    public static String Catcher_Operation_To_W_S = "0110";

    public static String Catcher_Operation_To_W_N = "0111";

    public static String Catcher_Operation_Conti_Stop_E_W = "0012";

    public static String Catcher_Operation_Conti_Stop_N_S = "0013";

    public static String Catcher_Operation_Conti_Stop_All = "0014";

    /****
     * 定义Redis中的 Key
     ***/
    public static String Reids_Key_CatcherStatus = "CatcherStatus";
}
