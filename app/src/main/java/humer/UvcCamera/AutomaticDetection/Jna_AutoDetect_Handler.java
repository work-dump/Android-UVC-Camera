package humer.UvcCamera.AutomaticDetection;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import humer.UvcCamera.SetUpTheUsbDeviceUsbIso;


public class Jna_AutoDetect_Handler {

    private Context mContext;
    private Activity activity;
    private SetUpTheUsbDeviceUsbIso setUpTheUsbDeviceUsbIso;

    public static int sALT_SETTING;
    public static int smaxPacketSize ;
    public static int scamFormatIndex ;   // MJPEG // YUV // bFormatIndex: 1 = uncompressed
    public static String svideoformat;
    public static int scamFrameIndex ; // bFrameIndex: 1 = 640 x 360;       2 = 176 x 144;     3 =    320 x 240;      4 = 352 x 288;     5 = 640 x 480;
    public static int simageWidth;
    public static int simageHeight;
    public static int scamFrameInterval ; // 333333 YUV = 30 fps // 666666 YUV = 15 fps
    public static int spacketsPerRequest ;
    public static int sactiveUrbs ;
    public static String sdeviceName;
    public static byte bUnitID;
    public static byte bTerminalID;
    public static byte[] bNumControlTerminal;
    public static byte[] bNumControlUnit;
    public static byte[] bcdUVC;
    public static byte bStillCaptureMethod;
    private static boolean libUsb;

    private static int spacketCnt;
    private static int spacket0Cnt ;
    private static int spacket12Cnt;
    private static int spacketDataCnt;
    private static int spacketHdr8Ccnt;
    private static int spacketErrorCnt ;
    private static int sframeCnt ;
    private static int sframeLen ;
    private static int [] sframeLenArray;
    private static int [] [] shighestFramesCube;
    private static int srequestCnt = 0;
    private static int sframeMaximalLen = 0;
    private static boolean fiveFrames;
    // how many transfers completed
    private static int doneTransfers;
    private static boolean highQuality;
    public static boolean maxPacketsPerRequestReached;
    public static boolean maxActiveUrbsReached;
    private static String progress;
    private static boolean submiterror;




    private static boolean max_Framelength_cant_reached;


    public Jna_AutoDetect_Handler(SetUpTheUsbDeviceUsbIso setUpTheUsbDeviceUsbIso, Context mContext) {
        this.setUpTheUsbDeviceUsbIso = setUpTheUsbDeviceUsbIso;
        this.mContext = mContext;
        fetchTheValues();


    }

    //  return -1 == error
    //  return 0 == sucess
    //  return 1 == startAutoDetection
    public int compare() {
        fetchTheValues();
        if (submiterror) {
            setUpTheUsbDeviceUsbIso.transferSucessful = false;
            int i = solveSubmitError();
            writeTheValues();
            return i;
        }
        else {
            setUpTheUsbDeviceUsbIso.transferSucessful = true;
            setUpTheUsbDeviceUsbIso.sucessfulDoneTransfers ++;
        }







        if (is_framelength_as_long_as_expected_maxSize()) {
            if (!fiveFrames) {
                fiveFrames = true;
                writeTheValues();
                return 1;
            } else {
                if (!highQuality) {
                    highQuality = true;
                    writeTheValues();
                    return 1;
                } else {
                    writeTheValues();
                    return 0;
                }
            }
        }
        if (!setUpTheUsbDeviceUsbIso.maxPacketsPerRequestReached) {
            switch (spacketsPerRequest) {
                case 1:
                    setUpTheUsbDeviceUsbIso.progress = "3% done";
                    spacketsPerRequest = 2;
                    writeTheValues();
                    return 1;
                case 2:
                    setUpTheUsbDeviceUsbIso.progress = "5% done";

                    spacketsPerRequest = 4;
                    writeTheValues();
                    return 1;
                case 4:
                    setUpTheUsbDeviceUsbIso.progress = "8% done";
                    spacketsPerRequest = 8;
                    writeTheValues();
                    return 1;
                case 8:
                    setUpTheUsbDeviceUsbIso.progress = "10% done";
                    spacketsPerRequest = 16;
                    writeTheValues();
                    return 1;
                case 16:
                    setUpTheUsbDeviceUsbIso.progress = "12% done";
                    spacketsPerRequest = 32;
                    writeTheValues();
                    return 1;
                case 32:
                    maxPacketsPerRequestReached = true;
                    setUpTheUsbDeviceUsbIso.maxPacketsPerRequestReached = true;
                    break;
            }
        }

        if (!setUpTheUsbDeviceUsbIso.maxActiveUrbsReached) {
            switch (sactiveUrbs) {
                case 1:
                    setUpTheUsbDeviceUsbIso.progress = "20% done";
                    sactiveUrbs = 2;
                    writeTheValues();
                    return 1;
                case 2:
                    setUpTheUsbDeviceUsbIso.progress = "30% done";
                    sactiveUrbs = 4;
                    writeTheValues();
                    return 1;
                case 4:
                    setUpTheUsbDeviceUsbIso.progress = "40% done";
                    sactiveUrbs = 8;
                    writeTheValues();
                    return 1;
                case 8:
                    setUpTheUsbDeviceUsbIso.progress = "50% done";
                    sactiveUrbs = 16;
                    writeTheValues();
                    return 1;
                case 16:
                    setUpTheUsbDeviceUsbIso.progress = "60% done";
                    sactiveUrbs = 32;
                    writeTheValues();
                    return 1;
                case 32:
                    setUpTheUsbDeviceUsbIso.progress = "70% done";
                    maxActiveUrbsReached = true;
                    setUpTheUsbDeviceUsbIso.maxActiveUrbsReached = true;
                    break;
            }
        }
        writeTheValues();
        return -1;
    }

    private int solveSubmitError() {
        if (setUpTheUsbDeviceUsbIso.sucessfulDoneTransfers > 1) {
            if (setUpTheUsbDeviceUsbIso.last_transferSucessful) {
               restoreLastValues();
               return -1;
            }
        }
        return -1;
    }

    private void restoreLastValues() {
        setUpTheUsbDeviceUsbIso.last_camStreamingAltSetting = sALT_SETTING;
        setUpTheUsbDeviceUsbIso.last_camFormatIndex = scamFormatIndex;
        setUpTheUsbDeviceUsbIso.last_camFrameIndex = scamFrameIndex;
        setUpTheUsbDeviceUsbIso.last_camFrameInterval = scamFrameInterval;
        setUpTheUsbDeviceUsbIso.last_packetsPerRequest = spacketsPerRequest;
        setUpTheUsbDeviceUsbIso.last_maxPacketSize = smaxPacketSize;
        setUpTheUsbDeviceUsbIso.last_imageWidth = simageWidth;
        setUpTheUsbDeviceUsbIso.last_imageHeight = simageHeight;
        setUpTheUsbDeviceUsbIso.last_activeUrbs = sactiveUrbs;
        setUpTheUsbDeviceUsbIso.last_videoformat = svideoformat;

    }

    private boolean is_framelength_as_long_as_expected_maxSize() {
        int maxSize = simageWidth * simageWidth *2;
        if (!fiveFrames) {
            if (sframeLen >= maxSize) return true;
            else return false;
        } else {
            if (sframeLenArray == null) return false;
            if ((sframeLenArray[0] >= maxSize & sframeLenArray[1] >= maxSize & sframeLenArray[2] >= maxSize & sframeLenArray[3] >= maxSize & sframeLenArray[4] >= maxSize )) return true;
            else return false;
        }
    }

    public void fetchTheValues() {
        if (setUpTheUsbDeviceUsbIso != null) {
            sALT_SETTING = setUpTheUsbDeviceUsbIso.camStreamingAltSetting;
            svideoformat = setUpTheUsbDeviceUsbIso.videoformat;
            scamFormatIndex = setUpTheUsbDeviceUsbIso.camFormatIndex;
            simageWidth = setUpTheUsbDeviceUsbIso.imageWidth;
            simageHeight = setUpTheUsbDeviceUsbIso.imageHeight;
            scamFrameIndex = setUpTheUsbDeviceUsbIso.camFrameIndex;
            scamFrameInterval = setUpTheUsbDeviceUsbIso.camFrameInterval;
            spacketsPerRequest = setUpTheUsbDeviceUsbIso.packetsPerRequest;
            smaxPacketSize = setUpTheUsbDeviceUsbIso.maxPacketSize;
            sactiveUrbs = setUpTheUsbDeviceUsbIso.activeUrbs;
            sdeviceName = setUpTheUsbDeviceUsbIso.deviceName;
            bUnitID = setUpTheUsbDeviceUsbIso.bUnitID;
            bTerminalID = setUpTheUsbDeviceUsbIso.bTerminalID;
            bNumControlTerminal = setUpTheUsbDeviceUsbIso.bNumControlTerminal;
            bNumControlUnit = setUpTheUsbDeviceUsbIso.bNumControlUnit;
            bcdUVC = setUpTheUsbDeviceUsbIso.bcdUVC;
            bStillCaptureMethod = setUpTheUsbDeviceUsbIso.bStillCaptureMethod;
            libUsb = setUpTheUsbDeviceUsbIso.libUsb;
            progress = setUpTheUsbDeviceUsbIso.progress;
            submiterror = setUpTheUsbDeviceUsbIso.submiterror;
            sframeLenArray = setUpTheUsbDeviceUsbIso.sframeLenArray;


            spacketCnt = setUpTheUsbDeviceUsbIso.spacketCnt;
            spacket0Cnt = setUpTheUsbDeviceUsbIso.spacket0Cnt;
            spacket12Cnt = setUpTheUsbDeviceUsbIso.spacket12Cnt;
            spacketDataCnt = setUpTheUsbDeviceUsbIso.spacketDataCnt;
            spacketHdr8Ccnt = setUpTheUsbDeviceUsbIso.spacketHdr8Ccnt;
            spacketErrorCnt = setUpTheUsbDeviceUsbIso.spacketErrorCnt;
            sframeCnt = setUpTheUsbDeviceUsbIso.sframeCnt;
            sframeLen = setUpTheUsbDeviceUsbIso.sframeLen;
            srequestCnt = setUpTheUsbDeviceUsbIso.srequestCnt;
            fiveFrames = setUpTheUsbDeviceUsbIso.fiveFrames;
            doneTransfers = setUpTheUsbDeviceUsbIso.doneTransfers;
            highQuality = setUpTheUsbDeviceUsbIso.highQuality;
            max_Framelength_cant_reached = setUpTheUsbDeviceUsbIso.max_Framelength_cant_reached;
            maxPacketsPerRequestReached = setUpTheUsbDeviceUsbIso.maxPacketsPerRequestReached;
            maxActiveUrbsReached = setUpTheUsbDeviceUsbIso.maxActiveUrbsReached;


        }
    }

    public void writeTheValues() {
        if (setUpTheUsbDeviceUsbIso != null) {
            setUpTheUsbDeviceUsbIso.packetsPerRequest = spacketsPerRequest;
            setUpTheUsbDeviceUsbIso.activeUrbs = sactiveUrbs;
            setUpTheUsbDeviceUsbIso.fiveFrames = fiveFrames;
            setUpTheUsbDeviceUsbIso.highQuality = highQuality;
            setUpTheUsbDeviceUsbIso.max_Framelength_cant_reached = max_Framelength_cant_reached;
            setUpTheUsbDeviceUsbIso.sframeLenArray = sframeLenArray;


            // other values
            setUpTheUsbDeviceUsbIso.camStreamingAltSetting = sALT_SETTING;
            setUpTheUsbDeviceUsbIso.videoformat = svideoformat;
            setUpTheUsbDeviceUsbIso.camFormatIndex = scamFormatIndex;
            setUpTheUsbDeviceUsbIso.imageWidth = simageWidth;
            setUpTheUsbDeviceUsbIso.imageHeight = simageHeight;
            setUpTheUsbDeviceUsbIso.camFrameIndex = scamFrameIndex;
            setUpTheUsbDeviceUsbIso.camFrameInterval = scamFrameInterval;
            setUpTheUsbDeviceUsbIso.maxPacketSize = smaxPacketSize;
            setUpTheUsbDeviceUsbIso.deviceName = sdeviceName;
            setUpTheUsbDeviceUsbIso.bUnitID = bUnitID;
            setUpTheUsbDeviceUsbIso.bTerminalID = bTerminalID;
            setUpTheUsbDeviceUsbIso.bNumControlTerminal = bNumControlTerminal;
            setUpTheUsbDeviceUsbIso.bNumControlUnit = bNumControlUnit;
            setUpTheUsbDeviceUsbIso.bcdUVC = bcdUVC;
            setUpTheUsbDeviceUsbIso.bStillCaptureMethod = bStillCaptureMethod;
            setUpTheUsbDeviceUsbIso.libUsb = libUsb;
            setUpTheUsbDeviceUsbIso.maxPacketsPerRequestReached = maxPacketsPerRequestReached;
            setUpTheUsbDeviceUsbIso.maxActiveUrbsReached = maxActiveUrbsReached;

        }

    }

    private void findHighestFrameLengths() {

        // find the highest Transferlength:
        int[] lengthOne = findHighestLength();



        if (lengthOne[1] == 0) {
            sactiveUrbs = 4;
            spacketsPerRequest = 4;
            log("4 / 4");
        } else if (lengthOne[1] == 0) {
            sactiveUrbs = 16;
            spacketsPerRequest = 16;
            lengthOne = findHighestLength();
        }





        log("lengthOne[0] = " + lengthOne[0]);
        // Test lowest package size ...
        setTheMaxPacketSize(false, true, 0);



        int[] lengthTwo = findHighestLength();


        log("lengthTwo[0] = " + lengthTwo[0]);
        if (lengthOne[0] > lengthTwo[0]) {
            log("lengthOne[0] > lengthTwo[0]  -->  " + lengthOne[0] + " > " + lengthTwo[0]);
            setTheMaxPacketSize(true, false, 0);
            if (lengthOne[1] == 0) {
                sactiveUrbs = 16;
                spacketsPerRequest = 16;
            } else if (lengthOne[1] == 1) {
                sactiveUrbs = 4;
                spacketsPerRequest = 4;
            }
        } else {
            log("lengthOneo[0] < lengthTwo[0]  -->  " + lengthOne[0] + " > " + lengthTwo[0]);
            if (lengthTwo[1] == 0) {
                sactiveUrbs = 16;
                spacketsPerRequest = 16;
            } else if (lengthTwo[1] == 1) {
                sactiveUrbs = 4;
                spacketsPerRequest = 4;
            }
        }

        //finalAutoMethod();

    }

    private void setTheMaxPacketSize (boolean highest, boolean lowest, int value) {

        if (highest) {
            int[] maxPacketsSizeArray = setUpTheUsbDeviceUsbIso.convertedMaxPacketSize.clone();
            int minValue = maxPacketsSizeArray[0];
            int minPos = 0;
            for (int i = 0; i < maxPacketsSizeArray.length; i++) {
                if (maxPacketsSizeArray[i] < minValue) {
                    minValue = maxPacketsSizeArray[i];
                    minPos = i;
                }
            }
            sALT_SETTING = (minPos + 1);
            smaxPacketSize = maxPacketsSizeArray[minPos];
        } else if (lowest) {
            int[] maxPacketsSizeArray = setUpTheUsbDeviceUsbIso.convertedMaxPacketSize.clone();
            int maxValue = maxPacketsSizeArray[0];
            int maxPos = 0;
            for (int i = 0; i < maxPacketsSizeArray.length; i++) {
                if (maxPacketsSizeArray[i] < maxValue) {
                    maxValue = maxPacketsSizeArray[i];
                    maxPos = i;
                }
            }
            sALT_SETTING = (maxPos + 1);
            smaxPacketSize = maxPacketsSizeArray[maxPos];
        } else {
            int[] maxPacketsSizeArray = setUpTheUsbDeviceUsbIso.convertedMaxPacketSize.clone();
            if (maxPacketsSizeArray.length >= value) {
                sALT_SETTING = (value + 1);
                smaxPacketSize = maxPacketsSizeArray[value];
            }
        }
    }

    private int [] findHighestLength () {
        int lenght;
        int highestlength = 0;
        int num = 0;
        for (int i = 0; i < sframeCnt; i++) {
            /*
            lenght

            lenght = shighestFramesCube[i][0] + shighestFramesCube[i][1] + shighestFramesCube[i][2] + shighestFramesCube[i][3] + shighestFramesCube[i][4];
            if(lenght > highestlength) {
                highestlength = lenght;
                num = i;
            }

             */
        }
        int [] ret = new int [2];
        ret[0] = highestlength;
        ret[1] = num;
        return ret;
    }

    private void log(String msg) {
        Log.i("Jna_AutoDetect_Handler", msg);
    }
}
