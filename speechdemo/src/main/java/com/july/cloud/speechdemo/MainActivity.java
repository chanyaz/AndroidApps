package com.july.cloud.speechdemo;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech tts;
    private Button bt_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
            }
        });
        tts = new TextToSpeech(this,new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status) {
                //如果装载TTS引擎成功
                if(status == TextToSpeech.SUCCESS)
                {
                    //设置使用美式英语朗读(虽然设置里有中文选项Locale.Chinese,但并不支持中文)
                    int result = tts.setLanguage(Locale.UK);
                    tts.setSpeechRate(0.8f);

                    //如果不支持设置的语言
                    if(result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE)
                    {
                        Toast.makeText(MainActivity.this, "TTS暂时不支持这种语言朗读", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        final TextView tv_speech = (TextView) findViewById(R.id.tv_speech);
        tv_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //执行朗读
                tts.speak(tv_speech.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){

                    @Override
                    public void onStart(String s) {
                        bt_stop.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDone(String s) {
                        bt_stop.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }
        });
       /* record.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                //将朗读文本的音频记录到指定文件
                tts.synthesizeToFile(editText.toString().toString(), null, "/sdcard/sound.wav");
                Toast.makeText(Speech.this, "声音记录成功", 50000).show();
            }});*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭TextToSpeech对象
        if(tts != null)
            tts.shutdown();
    }
}
