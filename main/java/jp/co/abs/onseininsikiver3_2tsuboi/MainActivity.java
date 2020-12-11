package jp.co.abs.onseininsikiver3_2tsuboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //UI
    ImageView imageview;
    EditText onseiResult;
    ListView listView;

    //音声認識結果のリスト
    ArrayList<String> data;
    String resultsString = "";
    Intent intent; // SpeechRecognizerに渡すIntent
    SpeechRecognizer recognizer;
    int BUTTON_STATUS = 0;

    //音声の翻訳
    ArrayList<String> translateText = new ArrayList<>();
    private HttpGetTask task;
    private HttpGetTask1 task1;
    private HttpGetTask2 task2;
    private HttpGetTask3 task3;
    private HttpGetTask4 task4;

    //テキスト読み上げ
    TextToSpeech tts;
    float pitch = 1.0f;
    float rate = 1.0f;
    int SPEECH_STATUS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview = findViewById(R.id.mic);
        imageview.setOnClickListener(onClick_button);

        onseiResult = findViewById(R.id.onsei);
        onseiResult.setFocusable(false);
        onseiResult.setEnabled(false);
        onseiResult.setTextColor(Color.BLACK);

        listView = findViewById(R.id.list);
        listView.setOnItemClickListener(onClick_item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "マイクボタンをタップしてください", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 音声認識中にアプリから離れた場合、認識結果を破棄
        if (data.size() != 0) {
            data.remove(data.size() - 1);
        }

        stopListening();
    }

    @Override
    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            Locale locale;
            switch(SPEECH_STATUS) {
                case 0:
                    locale = Locale.ENGLISH;
                    tts.setLanguage(locale);
                    break;
                case 1:
                    locale = Locale.FRENCH;
                    tts.setLanguage(locale);
                    break;
                case 2:
                    locale = Locale.ITALY;
                    tts.setLanguage(locale);
                    break;
                case 3:
                    locale = Locale.GERMANY;
                    tts.setLanguage(locale);
                    break;
                case 4:
                    locale = Locale.CHINESE;
                    tts.setLanguage(locale);
                    break;
            }
        }else{
            Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
        }
    }

    // テキスト読み上げ
    private AdapterView.OnItemClickListener onClick_item = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch(i){
                case 0:
                    SPEECH_STATUS = 0;
                    break;
                case 1:
                    SPEECH_STATUS = 1;
                    break;
                case 2:
                    SPEECH_STATUS = 2;
                    break;
                case 3:
                    SPEECH_STATUS = 3;
                    break;
                case 4:
                    SPEECH_STATUS = 4;
                    break;
            }

            tts = new TextToSpeech(MainActivity.this,MainActivity.this);
            tts.setPitch(pitch);
            tts.setSpeechRate(rate);
            if(tts.isSpeaking()){
                tts.stop();
            }
            String speechText = (String) listView.getSelectedItem();
            tts.speak(speechText,TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    private View.OnClickListener onClick_button = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (BUTTON_STATUS) {
                case 0:
                    BUTTON_STATUS = 1;
                    // 入力テキストがある場合、削除
                    if(resultsString != ""){
                        onseiResult.setText(null);
                        resultsString = "";
                    }
                    //翻訳結果がリスト表示されている場合、削除
                    if(translateText.size() != 0){
                        translateText.clear();
                        listView.setAdapter(null);
                    }
                    startListening();
                    break;
                case 1:
                    BUTTON_STATUS = 0;
                    stopListening();
                    if (resultsString != "") {
                        TextView textView = findViewById(R.id.resultText);
                        textView.setText("音声を聞く: リスト内テキストをタップ");

                        task = new HttpGetTask(translateText,getApplicationContext());
                        task1 = new HttpGetTask1(translateText,getApplicationContext());
                        task2 = new HttpGetTask2(translateText,getApplicationContext());
                        task3 = new HttpGetTask3(translateText,getApplicationContext());
                        task4 = new HttpGetTask4(translateText,getApplicationContext());

                        task.execute(getURL("&source=ja&target=en"));
                        task1.execute(getURL("&source=ja&target=fr"));
                        task2.execute(getURL("&source=ja&target=it"));
                        task3.execute(getURL("&source=ja&target=de"));
                        task4.execute(getURL("&source=ja&target=ch"));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list, translateText);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public String getURL(String language){
        String masterURL = "https://script.google.com/macros/s/AKfycbxCtPdIyjXALRciL_wmv_XY7unDCGGg2ZKn0QZLkLsJdSFc_3s/exec?text=";
        String APIUrl = masterURL + resultsString + language;

        return APIUrl;
    }

    protected void startListening() {
        try {
            if (recognizer == null) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "音声認識が使えません",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                recognizer.setRecognitionListener(new listener());
            }

            if (getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() == 0) {
                return;
            }
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            recognizer.startListening(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 音声認識を終了する
    protected void stopListening() {
        if (recognizer != null) recognizer.destroy();
        recognizer = null;
    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopListening();
        startListening();
    }

    // 音声認識クラス
    class listener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "音声を入力してください", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            imageview.setImageResource(R.drawable.mic2);
        }

        @Override
        public void onEndOfSpeech() {
            imageview.setImageResource(R.drawable.mic);
        }

        @Override
        public void onResults(Bundle results) {
            data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                resultsString += data.get(i);
            }
            onseiResult.setText(resultsString);
            Toast.makeText(getApplicationContext(), "マイクボタンをタップしてください", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int error) {
            Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();

            // 音声認識を繰り返す
            restartListeningService();
        }

        // その他のメソッド RecognitionListenerの特性上記述が必須
        public void onRmsChanged(float v) {}
        public void onBufferReceived(byte[] bytes) {}
        public void onPartialResults(Bundle bundle) {}
        public void onEvent(int i, Bundle bundle) {}
    }
}

