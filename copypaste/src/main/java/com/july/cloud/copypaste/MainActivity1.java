package com.july.cloud.copypaste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity1 extends AppCompatActivity {
    private TextView tv_copy;
    private EditText et_paste;
    final int Menu_1 = Menu.FIRST;
    final int Menu_2 = Menu.FIRST + 1;
    private ClipboardManager mClipboard = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_copy = (TextView) findViewById(R.id.tv_copy);
        et_paste = (EditText) findViewById(R.id.et_paste);
        registerForContextMenu(tv_copy);

    }

    //创建ContextMenu菜单的回调方法
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(m,v,menuInfo);

        //在上下文菜单选项中添加选项内容
        //add方法的参数：add(分组id,itemid, 排序, 菜单文字)
        m.add(0, Menu_1, 0, "Copy");
        m.add(0, Menu_2, 0, "Dictionary");
    }
    private void copyFrom() {

        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        }

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",tv_copy.getText());

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
            Toast.makeText(MainActivity1.this,
                    "Clipboard is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            ClipData clipData = mClipboard.getPrimaryClip();
            int count = clipData.getItemCount();

            for (int i = 0; i < count; ++i) {

                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item
                        .coerceToText(MainActivity1.this);
                Log.i("mengdd", "item : " + i + ": " + str);

                resultString += str;
            }

        }
        Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show();
        et_paste.setText(resultString);
    }


    //ContextMenu菜单选项的选项选择的回调事件
    public boolean onContextItemSelected(MenuItem item) {
        //参数为用户选择的菜单选项对象
        //根据菜单选项的id来执行相应的功能
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(this, "Copy", Toast.LENGTH_SHORT).show();
                copyFrom();
                break;
            case 2:
                Toast.makeText(this, "Translate", Toast.LENGTH_SHORT).show();
                pasteToResult();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
