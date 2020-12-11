package jp.co.abs.onseininsikiver3_2tsuboi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public final class HttpGetTask3 extends AsyncTask<String, Void, String> {

    private ArrayList<String> translateText;
    private Context mContext;

    public HttpGetTask3(ArrayList<String> translateText, Context context){
        super();
        this.translateText = translateText;
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        // 取得したテキストを格納する変数
        final StringBuilder result = new StringBuilder();
        HttpURLConnection con = null;
        try {
            URL url = new URL(strings[0]);
            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            //    con.setDoInput(true);
            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // テキストを取得する
                final InputStream in = con.getInputStream();
              //  final String encoding = con.getContentEncoding();
                final InputStreamReader inReader = new InputStreamReader(in,"utf-8");
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

        try{
            JSONObject json = new JSONObject(result);
            String translatedText = json.getString("text");

            if(translatedText != ""){
                translateText.add(translatedText);
            }else{
                Toast.makeText(mContext,"ドイツ語の翻訳に失敗しました",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}