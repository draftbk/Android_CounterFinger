package com.example.slf.counterfinger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnProc;
    private ImageView imageView;
    private Bitmap bmp;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        btnProc= (Button) findViewById(R.id.btn);
        imageView= (ImageView) findViewById(R.id.image);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
        imageView.setImageBitmap(bmp);
        btnProc.setOnClickListener(this);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn:
                Intent intent=new Intent(MainActivity.this,OpenCameraActivity.class);
                startActivity(intent);
                Mat frame = new Mat();
                Utils.bitmapToMat(bmp, frame);
                doCanny(frame);

                break;
        }
    }
    private Mat doCanny(Mat frame)
    {
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(grayImage,bmp);
        imageView.setImageBitmap(bmp);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        // canny detector, with ratio of lower:upper threshold of 3:1
//        Imgproc.Canny(detectedEdges, detectedEdges, this.threshold.getValue(), this.threshold.getValue() * 3);

        // using Canny's output as a mask, display the result
        Mat dest = new Mat();
        frame.copyTo(dest, detectedEdges);

        return dest;
    }


}
