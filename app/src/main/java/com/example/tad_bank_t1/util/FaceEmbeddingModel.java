package com.example.tad_bank_t1.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class FaceEmbeddingModel {
    private static FaceEmbeddingModel INSTANCE;
    private final Interpreter tflite;
    private FaceEmbeddingModel(Context context){
        try {
            MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, "facenet.tflite");
            Interpreter.Options opt = new Interpreter.Options();
            opt.setNumThreads(4);
            tflite = new Interpreter(modelBuffer, opt);
        }catch (IOException e){
            throw new RuntimeException("Không tải được model TFLite: " + e.getMessage());
        }
    }
    public static synchronized FaceEmbeddingModel getInstance(Context context){
        if (INSTANCE == null) INSTANCE = new FaceEmbeddingModel(context.getApplicationContext());
        return INSTANCE;
    }
    public float[] embed(Bitmap bmp160){
        float[][][][] input = new float[1][160][160][3];
        for (int y = 0; y<160; y++){
            for (int x = 0; x<160; x++){
                int pixel = bmp160.getPixel(x, y);
                float r = ((pixel >> 16) & 0xFF) / 255f;
                float g = ((pixel >> 8) & 0xFF) / 255f;
                float b = (pixel & 0xFF) / 255f;
                input[0][y][x][0] = (r - 0.5f) * 2.0f;
                input[0][y][x][1] = (g - 0.5f) * 2.0f;
                input[0][y][x][2] = (b - 0.5f) * 2.0f;
            }
        }
        float[][] output = new float[1][128];
        try {
            tflite.run(input, output);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        float norm = 0f;
        for (float v : output[0]) norm += v * v;
        norm = (float) Math.sqrt(norm) + 1e-6f;
        for (int i = 0; i < output[0].length; i++) output[0][i] /= norm;
        return output[0];
    }
}
