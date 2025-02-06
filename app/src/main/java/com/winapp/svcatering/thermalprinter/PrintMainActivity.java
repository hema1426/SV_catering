package com.winapp.svcatering.thermalprinter;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.winapp.svcatering.thermalprinter.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;
import static com.winapp.svcatering.thermalprinter.DeviceConnFactoryManager.CONN_STATE_FAILED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.printer.command.EscCommand;
import com.printer.command.LabelCommand;
import com.winapp.svcatering.R;

import java.util.ArrayList;
import java.util.Vector;


public class PrintMainActivity extends AppCompatActivity {

    private TextView mTvState;
    private String printerMacAddress;
    // Define the Context
    private Context context;
    // Define the Printer address
    // Define the Constructor
    private static final String FILE_NAME = "TEMP.LBL";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String LINEBREAK = "\r\b";
    private static final int LABEL_WIDTH = 574;
    private static final int FONT = 5;
    private static final int FONT_SIZE = 0;
    private static final int LEFT_MARGIN = 10;
    private static final int RIGHT_MARGIN = 564;
    private static final int LINE_THICKNESS = 2;
    private static final int LINE_SPACING = 40;
    private static final String CMD_TEXT = "T";
    private static final String CMD_LINE = "L";
    private static final String CMD_PRINT = "PRINT" + LINE_SEPARATOR;
    private static final String SPACE = " ";
    private static final int POSTFEED = 50;
    public static int PAPER_WIDTH = 78;

    /**
     * 权限请求码
     */
    private static final int REQUEST_CODE = 0x001;

    /**
     * 蓝牙所需权限
     */
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };

    /**
     * 未授予的权限
     */
    private ArrayList<String> per = new ArrayList<>();

    /**
     * 蓝牙请求码
     */
    public static final int BLUETOOTH_REQUEST_CODE = 0x002;

    private ThreadPool threadPool;//线程

    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private int id = 0;

    /**
     * 打印机是否连接
     */
    private static final int CONN_PRINTER = 0x003;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x004;

    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_main_layout);

        mTvState = findViewById(R.id.tv_state);

        checkPermission();
        requestPermission();

    }

    private void checkPermission() {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                per.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (per.size() > 0) {
            String[] p = new String[per.size()];
            ActivityCompat.requestPermissions(this, per.toArray(p), REQUEST_CODE);
        }
    }


    public void btnConnect(View view) {
        startActivityForResult(new Intent(PrintMainActivity.this, BluetoothListActivity.class), BLUETOOTH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //蓝牙连接
            if (requestCode == BLUETOOTH_REQUEST_CODE) {
                closePort();
                //获取蓝牙mac地址
                String macAddress = data.getStringExtra(BluetoothListActivity.EXTRA_DEVICE_ADDRESS);
                //初始化DeviceConnFactoryManager 并设置信息
                new DeviceConnFactoryManager.Build()
                        //设置标识符
                        .setId(id)
                        //设置连接方式
                        .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                        //设置连接的蓝牙mac地址
                        .setMacAddress(macAddress)
                        .build();
                //配置完信息，就可以打开端口连接了
                Log.i("TAG", "onActivityResult: \n" + "connect bluetooth" + id);
                threadPool = ThreadPool.getInstantiation();
                threadPool.addTask(new Runnable() {
                    @Override
                    public void run() {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                    }
                });
            }
        }
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closePort() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null && DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort != null) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * 注册接收连接状态的广播
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QUERY_PRINTER_STATE);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    /**
     * 连接状态的广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DeviceConnFactoryManager.ACTION_CONN_STATE.equals(action)) {
                int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                switch (state) {
                    case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                        if (id == deviceId) mTvState.setText("not connected");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                        mTvState.setText("connecting");
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
                        mTvState.setText("connected");
                        Toast.makeText(PrintMainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                        break;
                    case CONN_STATE_FAILED:
                        mTvState.setText("not connected");
                        Toast.makeText(PrintMainActivity.this, "\n" +
                                "Connection failed! Try again or restart the printer", Toast.LENGTH_SHORT).show();
                        break;
                }
                /* Usb连接断开、蓝牙连接断开广播 */
            } else if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id);
                        Toast.makeText(PrintMainActivity.this, "successfully disconnected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PRINTER_COMMAND_ERROR:
                    Toast.makeText(PrintMainActivity.this, "Please select the correct printer order", Toast.LENGTH_SHORT).show();
                    break;
                case CONN_PRINTER:
                    Toast.makeText(PrintMainActivity.this, "\n" +
                            "Please connect the printer first", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /**
     * 打印标签
     */
    public void btnPrint(View view) {
        printLabel();
    }

    public void printLabel() {
        Log.i("TAG", "准备打印");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //先判断打印机是否连接
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "start printing");
                    sendLabel();
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }


    private void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(0, 0); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);//开启带Response的打印，用于连续打印
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文

       //    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.aadhi_logo);
       //    tsc.addBitmap(100, 0, LabelCommand.BITMAP_MODE.XOR, 400, b);
        tsc.addLimitFeed(50);
        tsc.addBar(0,40,1000,2);
        tsc.addText(0, 70, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "NO PRODUCT                 PRICE   TOTAL");
        tsc.addText(0, 100, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "    ISS    RTN    NET      (SGD)   (SGD)");

        tsc.addLimitFeed(50);
      /*  tsc.addText(0, 110, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "__________________________________________________");*/
        tsc.addBar(0,130,1000,2);
        tsc.addLimitFeed(50);
        tsc.addText(0, 150, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "1   French Loaf - Pkt(6)                          ");
        tsc.addLimitFeed(50);
        tsc.addText(0, 180, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "     10     2     8        2.00    16.00");

   /*     tsc.addText(0, 150, LabelCommand.FONTTYPE.FONT_1, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                "price：" + "99.00");
        tsc.addText(0, 140, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "quantity：" + "99");
        tsc.addText(0, 190, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "date：" + "2020-02-02");*/

        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addLimitFeed(10);
        tsc.addPartialCutter(EscCommand.ENABLE.ON);
        /* 发送数据 */
        Vector<Byte> data = tsc.getCommand();
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            Log.i("TAG", "sendLabel: \n" +
                    "printer is empty");
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(data);
    }


    /*private void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(0, 0); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);//开启带Response的打印，用于连续打印
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区

        // 绘制简体中文
        tsc.addText(30, 30, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "this is the title");
        tsc.addText(200, 30, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "serial number：" + "1");

        tsc.addText(30, 90, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "price：" + "99.00");
        tsc.addText(30, 140, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "quantity：" + "99");
        tsc.addText(30, 190, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "date：" + "2020-02-02");

        // 绘制图片
     //   Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
     //   tsc.addBitmap(20, 50, LabelCommand.BITMAP_MODE.OVERWRITE, b.getWidth(), b);

        //二维码
    //    tsc.addQRCode(200, 90, LabelCommand.EEC.LEVEL_L, 4, LabelCommand.ROTATION.ROTATION_0, "www.baidu.com");

        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addLimitFeed(10);
        tsc.addPartialCutter(EscCommand.ENABLE.ON);

        *//* 发送数据 *//*
        Vector<Byte> data = tsc.getCommand();
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            Log.i("TAG", "sendLabel: \n" +
                    "printer is empty");
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(data);
    }*/

    /**
     * 多次打印
     */
    public void btnPrint3(View view) {
        printLabel3();
    }

    public void printLabel3() {
        Log.i("TAG", "准备打印");
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                //先判断打印机是否连接
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                        !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
                    mHandler.obtainMessage(CONN_PRINTER).sendToTarget();
                    return;
                }
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
                    Log.i("TAG", "开始打印");
                    //循环多次打印
                    for (int i = 0; i < 3; i++) {
                        sendLabel3(i);
                    }
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }

    private void sendLabel3(int count) {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(10, 30); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        //tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
      //  tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);//开启带Response的打印，用于连续打印
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区

        // 绘制简体中文
        tsc.addText(30, 30, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_10, LabelCommand.FONTMUL.MUL_10,
                "Title");
        tsc.addText(200, 30, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_10, LabelCommand.FONTMUL.MUL_10,
                "Serial：" + count);

        tsc.addText(30, 90, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_10, LabelCommand.FONTMUL.MUL_10,
                "Price：" + "99.00");
        tsc.addText(30, 140, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_10, LabelCommand.FONTMUL.MUL_10,
                "Qty：" + "99");
        tsc.addText(30, 190, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_10, LabelCommand.FONTMUL.MUL_10,
                "Date：" + "2020-02-02");

        //二维码
      //  tsc.addQRCode(200, 90, LabelCommand.EEC.LEVEL_L, 4, LabelCommand.ROTATION.ROTATION_0, "www.baidu.com");

        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响

        /* 发送数据 */
        Vector<Byte> data = tsc.getCommand();
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            Log.i("TAG", "sendLabel: 打印机为空");
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(data);
    }

    /**
     * 断开连接
     */
    public void btnDisConn(View view) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
                !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
            Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy");
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
            threadPool = null;
        }
    }

}
