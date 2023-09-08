package com.desarrollo.myapplication.apiadapter.rongta;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RongtaPrinterHelper {
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{0x1B, 0x45, 0};

    public static String getTestdata() {
        String Text = "\n\n\nYour Elo Touch Solutions\nPayPoint receipt printer is\nworking properly.";

        ArrayList<Byte> dataArray = new ArrayList<Byte>();
        dataArray.addAll(AlignCenter());
        dataArray.addAll(toByteArray(Text)); //Prints the Text

        String asciiData = "\n***ASCII***\n\n";
        for(int i = 33 ; i < 256 ; i++){
            asciiData = asciiData+" "+String.valueOf((char)i);
        }
        dataArray.addAll(toByteArray(asciiData)); //Prints all ASCII values

        String barData = "\n***BARCODE***\n";
        dataArray.addAll(toByteArray(barData));
        long number = (long) Math.floor(Math.random() * 900000000L) + 100000000L;
        dataArray.addAll(getBarCode(String.valueOf(number))); //Prints Barcode for corresponding random number.
        dataArray.addAll(toByteArray(String.valueOf(number)+"\n\n\n")); //Prints the random number.

        String response=dataArray.toString();

        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        String Result = new String(bytes);
        return Result;
    }

    static ArrayList<Byte> AlignCenter()
    {
        ArrayList<Byte> list = new ArrayList<Byte>();
        list.add(Byte.valueOf((byte) 27));
        list.add(Byte.valueOf((byte) 97));
        list.add(Byte.valueOf((byte) 1));
        return list;
    }

    static ArrayList<Byte> toByteArray(String string)
    {
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (byte byt : string.getBytes())
        {
            list.add(Byte.valueOf(byt));
        }
        // line feed
        list.add(Byte.valueOf((byte) 0x0A));
        list.add(Byte.valueOf((byte) 0x0D));
        return list;
    }
    static ArrayList<Byte> getBarCode(String Code)
    {
        String barcode = "A"+Code+"B";
        byte[] codeData =  barcode.getBytes();
        ArrayList<Byte> command = new ArrayList<Byte>();

        //Barcode Width
        command.add(Byte.valueOf((byte) 0x1D));
        command.add(Byte.valueOf((byte) 0x77));
        command.add(Byte.valueOf((byte) 0x03));

        //Height
        command.add(Byte.valueOf((byte) 0x1D));
        command.add(Byte.valueOf((byte) 0x68));
        command.add(Byte.valueOf((byte) 100));

        //Position
        command.add(Byte.valueOf((byte) 0x1D));
        command.add(Byte.valueOf((byte) 0x48));
        command.add(Byte.valueOf((byte) 0x00));

        //Barcode Type CODABAR
        command.add(Byte.valueOf((byte) 0x1D));
        command.add(Byte.valueOf((byte) 0x6B));
        command.add(Byte.valueOf((byte) 0x06));
        for (byte byt : codeData) {
            command.add(Byte.valueOf(byt));
        }
        command.add(Byte.valueOf((byte) 0x00));
        return command;
    }

    public static byte[] generatePrintImageCommands(Bitmap bmp) {

        //TODO : Handle Alpha field in image to get printed
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>(); //binaryString list
        StringBuffer sb;

        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("generatePrintImageCommands error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("generatePrintImageCommands error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        return arrayCopy(commandList);
    }

    private static byte[] arrayCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    private static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = BinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static String BinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        String hexStr = "0123456789ABCDEF";
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String generateBarcodeCommands(String barCode, boolean withHRI, int widthInDots, int heightInDots) {
        StringBuilder sb = new StringBuilder();

        int printHRI = (withHRI ? 50 : 48); //50: HRI Below the barcode, 48: HRI Not printed
        int[] sendCommands = {
                29, 119, widthInDots,     // set width
                29, 104, heightInDots,    // set height
                29, 72, printHRI,         // set HRI
                29, 107, 4};              // set symbology to code39

        for (int i = 0; i < sendCommands.length; i++) {
            sb.append((char) sendCommands[i]);
        }

        byte[] code = barCode.getBytes();

        for (int i = 0; i < code.length; ++i) {
            sb.append((char)code[i]);
        }

        sb.append((char) 0);
        return sb.toString();
    }
}
