package com.clay.bluetooth2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

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

    Button button00;
    Button button01;
    Button button02;
    Button button03;
    Button button04;
    Button button05;
    Button button06;
    Button button07;
    Button button08;
    Button button09;

    Button Button0A;//确定
    Button Button0B;//关锁
    Button Button0C;//修改密码
    Button Button033;//删除

    Button buttonSend;

    EditText editText;//发送数据文本
    private BluetoothSocket mmSocket;
    OutputStream mmOutStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button00 = (Button) findViewById(R.id.Button00);button00.setOnTouchListener(button00Touch);
        button01 = (Button) findViewById(R.id.Button01);button01.setOnTouchListener(button01Touch);
        button02 = (Button) findViewById(R.id.Button02);button02.setOnTouchListener(button02Touch);
        button03 = (Button) findViewById(R.id.Button03);button03.setOnTouchListener(button03Touch);
        button04 = (Button) findViewById(R.id.Button04);button04.setOnTouchListener(button04Touch);
        button05 = (Button) findViewById(R.id.Button05);button05.setOnTouchListener(button05Touch);
        button06 = (Button) findViewById(R.id.Button06);button06.setOnTouchListener(button06Touch);
        button07 = (Button) findViewById(R.id.Button07);button07.setOnTouchListener(button07Touch);
        button08 = (Button) findViewById(R.id.Button08);button08.setOnTouchListener(button08Touch);
        button09 = (Button) findViewById(R.id.Button09);button09.setOnTouchListener(button09Touch);

        Button0A = (Button) findViewById(R.id.Button0A);Button0A.setOnTouchListener(Button0ATouch);//确定
        Button0B = (Button) findViewById(R.id.Button0B);Button0B.setOnTouchListener(Button0BTouch);//关锁
        Button0C = (Button) findViewById(R.id.Button0C);Button0C.setOnTouchListener(Button0CTouch);//修改密码
        Button033 = (Button) findViewById(R.id.Button033);Button033.setOnTouchListener(Button033Touch);//删除

        buttonSend = (Button) findViewById(R.id.Button1);buttonSend.setOnClickListener(buttonSendClick);//发送

        editText = (EditText) findViewById(R.id.editText1);

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

        simpleAdapter.notifyDataSetChanged();
    }

    /***发送***/
    private View.OnClickListener buttonSendClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            try
            {
                mmOutStream.write(editText.getText().toString().getBytes());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    //删除
    private View.OnTouchListener Button033Touch = new View.OnTouchListener() {
        //java.lang.NullPointerException: Attempt to invoke virtual method 'void java.io.OutputStream.write(byte[])' on a null object reference

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ONC".getBytes());
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("O".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener Button0CTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("OND".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("O".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener Button0BTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ONC".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONF".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener Button0ATouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ONA".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("O".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button01Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON1".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONa".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button02Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON2".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONb".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button03Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON3".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONc".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button04Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON4".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONd".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button05Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON5".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONe".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button06Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON6".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONf".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button07Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON7".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONg".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button08Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON8".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONh".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button09Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ON9".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("ONi".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    private View.OnTouchListener button00Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    mmOutStream.write("ONE".getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "发送错误,请重新连接!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                try {
                    mmOutStream.write("E".getBytes());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_CANCEL) {

            }
            return false;
        }
    };

    /******listView选择*****/
    private AdapterView.OnItemClickListener listViewClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
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
                    //Toast.makeText(getApplicationContext(), "该设备已经绑定", 500).show();
//					//如果想要取消已经配对的设备，只需要将creatBond改为removeBond
                    Method method = BluetoothDevice.class.getMethod("createBond");
                    method.invoke(deviceList.get((int) id));
                }

                if (deviceList.get((int) id).getBondState() == BluetoothDevice.BOND_BONDED)
                {
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

                mmOutStream =mmSocket.getOutputStream();

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //新建的xml文件
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //根据不同的id点击不同按钮控制activity需要做的事件
        switch (item.getItemId())
        {
            case R.id. action_settings:
                deviceList.clear();
                listItem.clear();
                simpleAdapter.notifyDataSetChanged();
                editDeleteAlertDialog.show();
                mBluetoothAdapter.startDiscovery();//启用发现

                break;
        }
        return true;
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
