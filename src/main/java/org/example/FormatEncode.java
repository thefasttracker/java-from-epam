package org.example;

import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Formatter;

public class FormatEncode {
    public static void main(String[] args) throws IOException {
        final String filename1 = "/Users/ft2/projects/delMe/data/t.txt";
        final String filename2 = "data/t.txt";
        Formatter formatter = new Formatter();
        formatter.format("This %s is about %S %c", "book", "java", '2');
        System.out.println(formatter);

        String encrypted = "Pass_1";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytesEncoded = encoder.encode(encrypted.getBytes());
        BigInteger bigInt = new BigInteger(1, bytesEncoded);
        String resHex = bigInt.toString(16);
        System.out.println(resHex);

        Base64FileEncoderDecoder ed = new Base64FileEncoderDecoder();
        ed.fileEncode(filename2);
        ed.fileDecode(filename2 + ".encode");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }
}

class Base64FileEncoderDecoder {
    public String fileEncode(String filename) throws IOException {
        try (FileInputStream input = new FileInputStream(filename)) {
            Base64.Encoder encoder = Base64.getEncoder();
            try (OutputStream output = encoder.wrap(new FileOutputStream(filename + ".encode"))) {
                int bytes;
                while ((bytes = input.read()) != -1) {
                    output.write(bytes);
                }
            }
        }
    return filename + ".encode";
    }

    public String fileDecode(String filenameEncode) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filenameEncode + ".decode")) {
            Base64.Decoder decoder = Base64.getDecoder();
            try(InputStream input = decoder.wrap(new FileInputStream(filenameEncode))) {
                int bytes;
                while ((bytes = input.read()) != -1) {
                    fos.write(bytes); }
            } }
        return filenameEncode + ".decode"; }
}