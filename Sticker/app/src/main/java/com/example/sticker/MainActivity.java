package com.example.sticker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static MainActivity main;//当一个全局变量使用
    public hua hua;//这个view用来画图片和文字
    public EditText text;//用户输入的文字
    private RecyclerView recy;//滑动控件
    private List<Integer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.main);//设定布局
        hua = findViewById(R.id.hua);
        text = findViewById(R.id.text);
        recy = findViewById(R.id.recy);
        //设为横向滚动
        recy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //初始化表情图片，把他们的id存起来
        list = new ArrayList<Integer>();
        list.add(R.mipmap.p1);
        list.add(R.mipmap.p2);
        list.add(R.mipmap.p3);
        list.add(R.mipmap.p4);
        list.add(R.mipmap.p5);
        list.add(R.mipmap.p6);
        list.add(R.mipmap.p7);
        list.add(R.mipmap.p8);
        list.add(R.mipmap.p9);
        list.add(R.mipmap.p10);
        list.add(R.mipmap.p11);

        hua.img = BitmapFactory.decodeResource(getResources(), list.get(0));
        recy.setAdapter(new adapter(list));//设置适配器

        findViewById(R.id.xiao).setOnClickListener(new View.OnClickListener() {//添加Button事件 字体缩小
            @Override
            public void onClick(View v) {
                float f = hua.paint.getTextSize();
                if (f > 10) {
                    f--;
                }
                hua.paint.setTextSize(f);
                hua.invalidate();
            }
        });
        findViewById(R.id.da).setOnClickListener(new View.OnClickListener() {//添加Button事件 字体放大
            @Override
            public void onClick(View v) {
                float f = hua.paint.getTextSize();
                f++;
                hua.paint.setTextSize(f);
                hua.invalidate();
            }
        });
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {//添加Button事件 保存图片
            @Override
            public void onClick(View v) {
                hua.invalidate();//刷新画布
                hua.buildDrawingCache();//截图，区域是整个hua
                Bitmap img = hua.getDrawingCache();//获取调用buildDrawingCache()时截取的图片
                String name = "表情" + System.currentTimeMillis() + ".jpg";
                //文件的路径为 sd卡/表情包/
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/表情包/" + name);
                //判断文件父目录是否存在，不存在则创建
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                try {
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                    img.compress(Bitmap.CompressFormat.JPEG, 50, out);//参数（格式，品质，输出到流）
                    out.flush();
                    out.close();
                    Toast.makeText(getApplicationContext(), "图片已保存到" + f.getParent(), Toast.LENGTH_SHORT).show();

                    //发送一个广播通知图库刷新相册
                    Intent in = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f));//发送广播通知图库刷新amssf
                    sendBroadcast(in);//发送广播
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        text.setSelection(text.getText().toString().length());
        verifyStoragePermissions(this);//动态申请权限
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //动态申请sd卡权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

class adapter extends RecyclerView.Adapter<adapter.viewh> {//RecyclerView适配器
    private List<Integer> list;

    public adapter(List l) {
        list = l;
    }

    @Override
    public viewh onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        viewh h = new viewh(v);
        return h;
    }

    @Override
    public void onBindViewHolder(final viewh holder, int position) {
        final int i = list.get(position);
        holder.img.setImageResource(i);
        holder.itemView.setOnClickListener(new View.OnClickListener() {//更换图片点击事件
            @Override
            public void onClick(View v) {
                MainActivity.main.hua.img = BitmapFactory.decodeResource(MainActivity.main.getResources(), i);
                MainActivity.main.hua.invalidate();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewh extends RecyclerView.ViewHolder {
        ImageView img;
        View itemView;

        public viewh(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            this.itemView = itemView;
        }
    }
}