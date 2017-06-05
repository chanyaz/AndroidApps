package com.july.cloud.copypaste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cloud on 2017/6/5.
 */

public class MainActivity extends AppCompatActivity {
    private ActionMode actionMode;
    private ActionMode.Callback callback;
    private TextView copyArea;
    private EditText pasteArea;
    private List<Integer> menuIds = new ArrayList<>();
    private ClipboardManager cmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        menuIds.add(R.id.copy);
        menuIds.add(R.id.paste);

        copyArea = (TextView) findViewById(R.id.tv_copy);
        pasteArea = (EditText) findViewById(R.id.et_paste);
        cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.newmenu, menu);
                return true;
            }
          /* @Override
           public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               try {
                   Field mEditor = TextView.class.getDeclaredField("mEditor");//找到 TextView中的成员变量mEditor
                   mEditor.setAccessible(true);
                   Object object= mEditor.get(copyArea);//根具持有对象拿到mEditor变量里的值 （android.widget.Editor类的实例）
                   //--------------------显示选择控制工具------------------------------//
                   Class mClass=Class.forName("android.widget.Editor");//拿到隐藏类Editor；
                   Method method=mClass.getDeclaredMethod("getSelectionController");//取得方法  getSelectionController
                   method.setAccessible(true);//取消访问私有方法的合法性检查
                   Object resultobject=method.invoke(object);//调用方法，返回SelectionModifierCursorController类的实例

                   Method show=resultobject.getClass().getDeclaredMethod("show");//查找 SelectionModifierCursorController类中的show方法
                   show.invoke(resultobject);//执行SelectionModifierCursorController类的实例的show方法
                   copyArea.setHasTransientState(true);

                   //--------------------忽略最后一次TouchUP事件-----------------------------------------------//
                   Field  mSelectionActionMode=mClass.getDeclaredField("mDiscardNextActionUp");//查找变量Editor类中mDiscardNextActionUp
                   mSelectionActionMode.setAccessible(true);
                   mSelectionActionMode.set(object,true);//赋值为true

               } catch (Exception e) {
                   e.printStackTrace();
               }
               return false;//return false 隐藏actionMod菜单
           }*/
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (!menuIds.contains(item.getItemId()))
                        item.setVisible(false);
                }
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.copy:
                        int min = 0;
                        int max = copyArea.getText().length();
                        if (copyArea.isFocused()) {
                            final int start = copyArea.getSelectionStart();
                            final int end = copyArea.getSelectionEnd();

                            min = Math.max(0, Math.min(start, end));
                            max = Math.max(0, Math.max(start, end));
                        }

                        cmb.setPrimaryClip(ClipData.newPlainText("paste_content", copyArea.getText().subSequence(min, max)));

                        mode.finish();
                        return true;
                    case R.id.paste:
                        if (pasteArea.isFocusable()) {
                            int index = pasteArea.getSelectionStart();
                            Editable editable = pasteArea.getText();
                            editable.insert(index, cmb.getPrimaryClip().getItemAt(0).coerceToText(MainActivity.this));
                        }

                        mode.finish();
                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };

        copyArea.setTextIsSelectable(true);
        copyArea.setCustomSelectionActionModeCallback(callback);

        pasteArea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (actionMode != null) {
                    return false;
                }

               actionMode = startActionMode(callback, ActionMode.TYPE_FLOATING);
                //actionMode = startActionMode(callback);

                return true;
            }
        });
    }
}
