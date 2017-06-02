package com.july.cloud.translatedemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView trans_result;
    private EditText trans_resource;
    private String trans_result1;
    private static final String APP_ID = "20170602000050294";
    private static final String SECURITY_KEY = "v0FkKNrR8HMRyZedvpVp";
    private final int GET_DIGEST_URL = 1;
    final int Menu_1 = Menu.FIRST;
    final int Menu_2 = Menu.FIRST + 1;
    final int Menu_3 = Menu.FIRST + 2;
    private ClipboardManager mClipboard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        trans_result.setText(trans_result1);
    }

    private void init() {
        trans_result = (TextView) findViewById(R.id.trans_result);
        trans_resource = (EditText) findViewById(R.id.trans_resource);
        Message message = new Message();
        message.what = GET_DIGEST_URL;
        mHandler.sendMessage(message);
        //注册上下文菜单
        registerForContextMenu(trans_resource);
    }
    //创建ContextMenu菜单的回调方法
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(m,v,menuInfo);

        //在上下文菜单选项中添加选项内容
        //add方法的参数：add(分组id,itemid, 排序, 菜单文字)
        m.add(0, Menu_1, 0, "复制文字");
        m.add(0, Menu_2, 0, "粘贴文字");
        m.add(0, Menu_3, 0, "全选文字");
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DIGEST_URL:
                    final String[] str = new String[1];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TransApi api = new TransApi(APP_ID, SECURITY_KEY);
                            String query = "height";
                            System.out.println(api.getTransResult(query, "auto", "zh"));
                            str[0] = api.getTransResult(query, "auto", "zh").toString();

                        }
                    }).start();

                    break;
            }
        }

    };
    private void copyFromEditText1() {

        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        }

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",trans_resource.getText());

        // Set the clipboard's primary clip.
        mClipboard.setPrimaryClip(clip);
    }
    private void pasteToResult() {
        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }

        String resultString = "";
        // 检查剪贴板是否有内容
        if (!mClipboard.hasPrimaryClip()) {
            Toast.makeText(MainActivity.this,
                    "Clipboard is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            ClipData clipData = mClipboard.getPrimaryClip();
            int count = clipData.getItemCount();

            for (int i = 0; i < count; ++i) {

                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item
                        .coerceToText(MainActivity.this);
                Log.i("mengdd", "item : " + i + ": " + str);

                resultString += str;
            }

        }
        Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show();
        trans_resource.setText(resultString);
    }


    //ContextMenu菜单选项的选项选择的回调事件
    public boolean onContextItemSelected(MenuItem item) {
        //参数为用户选择的菜单选项对象
        //根据菜单选项的id来执行相应的功能
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(this, "复制文字", Toast.LENGTH_SHORT).show();
                copyFromEditText1();
                break;
            case 2:
                Toast.makeText(this, "粘贴文字", Toast.LENGTH_SHORT).show();
                pasteToResult();
                break;
            case 3:
                Toast.makeText(this, "全选文字", Toast.LENGTH_SHORT).show();
                trans_resource.selectAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
