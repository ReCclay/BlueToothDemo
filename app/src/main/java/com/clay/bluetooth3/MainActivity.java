package com.clay.bluetooth3;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //主视图
    Button buttonSend;
    Button buttonRecClear;
    Button buttonSendClear;
    EditText editTextSend;
    TextView textViewRead;

    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, Object>> listItem;

    AlertDialog editDeleteAlertDialog = null;//对话框
    View editDeleteView = null;//对话框视图

    ListView listView;
    Button cancelButton;//搜索设备对话框取消按钮
    Button searchButton;//搜索设备按钮
    ProgressBar progressBar;//搜索设备progressBar

    BluetoothAdapter mBluetoothAdapter;//本地蓝牙适配器
    private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    private BluetoothSocket mmSocket;
    OutputStream mmOutStream;
    InputStream mmInputStream;
    boolean connectServerFlag = true;//连接标志 1-表示接下来要进行连接，此时状态是断开
    //接收数据
    boolean ReadDataFlage=false;//接收数据标志
    byte[] Readbyte = new byte[1024];
    int ReadbyteLen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();//地理位置的动态权限申请

        editTextSend = (EditText) findViewById(R.id.editText11);
        editTextSend.setMovementMethod(ScrollingMovementMethod.getInstance());
        textViewRead = (TextView) findViewById(R.id.textView11);
        textViewRead.setMovementMethod(ScrollingMovementMethod.getInstance());

        buttonSend = (Button) findViewById(R.id.button11); buttonSend.setOnClickListener(btnSendClickListener);
        buttonRecClear = (Button) findViewById(R.id.button13); buttonRecClear.setOnClickListener(btnRecClearClickListener);
        buttonSendClear = (Button) findViewById(R.id.button12); buttonSendClear.setOnClickListener(btnSendClearClickListener);

        /*********搜索设备对话框*********/
        editDeleteAlertDialog = new AlertDialog.Builder(MainActivity.this).create();//创建一个对话框
        editDeleteView = View.inflate(MainActivity.this, R.layout.editdelete, null);//创建视图
        editDeleteAlertDialog.setView(editDeleteView);//对话框加载视图

        listView = (ListView) editDeleteView.findViewById(R.id.listView21);
        listView.setOnItemClickListener(listViewClick);

        progressBar = (ProgressBar) editDeleteView.findViewById(R.id.progressBar21);
        progressBar.setVisibility(View.INVISIBLE);//关闭进度条

        cancelButton = (Button) editDeleteView.findViewById(R.id.button21);
        cancelButton.setOnClickListener(cancelButtonClick);
        searchButton = (Button) editDeleteView.findViewById(R.id.button22);
        searchButton.setOnClickListener(searchButtonClick);

        listItem = new ArrayList<HashMap<String,Object>>();/*在数组中存放数据*/
        simpleAdapter = new SimpleAdapter(this, listItem, R.layout.item,
                new String[]{"ItemTitle","ItemText"}, new int[]{R.id.ItemTitle,R.id.ItemText});
        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();//刷新item
    }

    private void checkPermission() {
        //是否大于6.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经授权
            int Code_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int Code_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //授权结果判断
            if (Code_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else if(Code_ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            else {
                Toast.makeText(getApplicationContext(), "Anroid6.0以上设备已动态授权", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Anroid6.0以下设备无需动态授权", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //动态申请成功对应的code
                    Toast.makeText(this, "Anroid6.0以上设备动态授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "用户拒绝了权限，搜索周围蓝牙设备失效！", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }


    //发送数据
    private View.OnClickListener btnSendClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            try
            {
                mmOutStream.write(editTextSend.getText().toString().getBytes());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    //发送清空
    private View.OnClickListener btnSendClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editTextSend.setText("");
        }
    };

    //接收清空
    private View.OnClickListener btnRecClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            textViewRead.setText("");
        }
    };

    /******listView选择*****/
    private AdapterView.OnItemClickListener listViewClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (mBluetoothAdapter.isDiscovering())
            {
                mBluetoothAdapter.cancelDiscovery();//取消搜索
                progressBar.setVisibility(View.INVISIBLE);//关闭进度条
            }
            try
            {
                if (deviceList.get((int) id).getBondState() != BluetoothDevice.BOND_BONDED)
                {
					//如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                    Method method = BluetoothDevice.class.getMethod("createBond");
                    method.invoke(deviceList.get((int) id));
                }

                if (deviceList.get((int) id).getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    ReadDataFlage = false;
                    ConnectThread connectThread = new ConnectThread(deviceList.get((int) id));
                    connectThread.start();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    /***对话框搜索按钮***/
    private View.OnClickListener searchButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v)
        {
            deviceList.clear();
            listItem.clear();
            simpleAdapter.notifyDataSetChanged();
            editDeleteAlertDialog.show();
            mBluetoothAdapter.startDiscovery();//启用发现
        }
    };
    /***对话框取消按钮***/
    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mBluetoothAdapter.isDiscovering())
            {
                mBluetoothAdapter.cancelDiscovery();//取消搜索
                progressBar.setVisibility(View.INVISIBLE);//关闭进度条
            }
            else
            {
                editDeleteAlertDialog.cancel();
            }


        }
    };

    /****关于蓝牙的一些广播******/
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) //发现设备
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // 搜索到的不是已经绑定的蓝牙设备
                //if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    // 防止重复添加
                    if (deviceList.indexOf(device) == -1)
                    {
                        deviceList.add(device);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ItemImage", R.drawable.bluetooth0);//加入图片
                        map.put("ItemTitle", "设备名称:"+device.getName());
                        map.put("ItemText", "设备地址:"+device.getAddress());
                        listItem.add(map);

                        simpleAdapter.notifyDataSetChanged();

                    }
                }
                Log.e("BluetoothService", "搜索到设备");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Log.e("BluetoothService", "开始搜索");
                Toast.makeText(getApplicationContext(), "正在搜索设备", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);//打开进度条
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
//				Log.e("BluetoothService", "搜索完毕");
//				Toast.makeText(getApplicationContext(), "搜索完毕", 500).show();
                progressBar.setVisibility(View.INVISIBLE);//关闭进度条
            }
            else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState())
                {
                    case BluetoothDevice.BOND_NONE:
                        Log.e(getPackageName(), "取消配对");
                        Toast.makeText(getApplicationContext(), "取消配对", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.e(getPackageName(), "配对中");
                        Toast.makeText(getApplicationContext(), "配对中", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.e(getPackageName(), "配对成功");
                        Toast.makeText(getApplicationContext(), "配对成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    //接收数据
    class ReadDataThread extends Thread
    {
        public void run()
        {
            while(ReadDataFlage)
            {
                try
                {
                    ReadbyteLen = mmInputStream.read(Readbyte);

                    if (ReadbyteLen == -1)
                    {
                        Log.e("MainActivity", "接收任务错误");
                        mmSocket = null;
                        ReadDataFlage = false;
                    }
                    else if(ReadbyteLen > 0)//读取到服务器返回的正确的数据
                    {
                        byte[] Readbyte_buff = new byte[ReadbyteLen];

                        try{
                            for(int i=0; i<ReadbyteLen; i++) {
                                Readbyte_buff[i] = Readbyte[i];
                            }

                            String str = new String(Readbyte_buff);
                            Log.e(TAG, str);
                            textViewRead.append(str);
                            textViewRead.post(new Runnable() {//让滚动条向下移动,永远显示最新的数据
                                @Override
                                public void run() {
                                    final int scrollAmount = textViewRead.getLayout().getLineTop(textViewRead.getLineCount()) - textViewRead.getHeight();
                                    if (scrollAmount > 0)
                                        textViewRead.scrollTo(0, scrollAmount);
                                    else
                                        textViewRead.scrollTo(0, 0);
                                }
                            });
                        }
                        catch(Exception e)
                        {

                        }

                    }

                } catch (IOException e) {
                    Log.e("MainActivity", "接收任务错误");
                    ReadDataFlage = false;
                    mmSocket = null;
                }
            }
        }
    }

    /****连接服务器线程*****/
    public class ConnectThread extends Thread
    {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // 得到一个bluetoothsocket
            try
            {
                mmSocket = device.createRfcommSocketToServiceRecord
                        (UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            }
            catch (IOException e)
            {
                mmSocket = null;
            }
        }
        public void run()
        {
            try
            {
                runOnUiThread( new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "正在连接", Toast.LENGTH_SHORT).show();
                    }
                });
                // socket 连接,该调用会阻塞，直到连接成功或失败
                mmSocket.connect();
                connectServerFlag = false;
                mmOutStream =mmSocket.getOutputStream();

                //接收数据线程
                mmInputStream = mmSocket.getInputStream();//获取输入流
                ReadDataFlage = true;
                ReadDataThread readDataThread = new ReadDataThread();
                readDataThread.start();

                if (mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.cancelDiscovery();//取消搜索
                    progressBar.setVisibility(View.INVISIBLE);//关闭进度条
                }
                else
                {
                    editDeleteAlertDialog.cancel();
                }

                runOnUiThread( new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                try {//关闭这个socket
                    mmSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
        }
    }


    //首页右上角菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //新建的xml文件
        setIconsVisible(menu, true);//设置菜单添加图标有效
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //根据不同的id点击不同按钮控制activity需要做的事件
        switch (item.getItemId()) {
            case R.id.action_settings:
                //事件
                deviceList.clear();
                listItem.clear();
                simpleAdapter.notifyDataSetChanged();
                editDeleteAlertDialog.show();
                mBluetoothAdapter.startDiscovery();//启用发现
                break;
        }
        return true;
    }

    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//得到本地蓝牙
        if (mBluetoothAdapter == null)
        {
            Log.e("BluetoothService", "设备不支持蓝牙设备");
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙设备", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled())//打开蓝牙
        {
            mBluetoothAdapter.enable();
            Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enabler);
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);// 找到设备的广播
        registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 搜索完成的广播
        registerReceiver(broadcastReceiver, filter);// 注册广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);// 开始搜索
        registerReceiver(broadcastReceiver, filter);// 注册广播

        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//绑定状态发生变化
        registerReceiver(broadcastReceiver, filter);// 注册广播
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//绑定状态发生变化

        super.onStart();
    }

    @Override
    protected void onStop()
    {
        try {//关闭这个socket
            mmSocket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        super.onStop();
    }
}
