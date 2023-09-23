package com.example.chatbotmohit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBotPage_Activity extends AppCompatActivity {

    private List<Message> messageList;
    private EditText inputEt;
    private RecyclerView chatRv;
    private ImageButton sendBtn;
    private MessageAdapter adapter;
    private TextView welcomeTxt;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_page);
        Objects.requireNonNull(getSupportActionBar()).setTitle("ChatBot");
        messageList = new ArrayList<>();

        {
            inputEt = findViewById(R.id.inputEt);
            chatRv = findViewById(R.id.chatRv);
            sendBtn = findViewById(R.id.sendBtn);
            welcomeTxt = findViewById(R.id.welcomeMsg);
        }       //find views

        //setting up recycler view
        adapter = new MessageAdapter(messageList);
        chatRv.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        chatRv.setLayoutManager(layoutManager);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prompt = inputEt.getText().toString();
                addToChatList(prompt, Message.SENT_BY_ME);
                welcomeTxt.setVisibility(View.GONE);
                inputEt.setText("");
                callApi(prompt);
            }
        });
    }

    private void addToChatList(String prompt, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(prompt, sentBy));
                adapter.notifyDataSetChanged();
                chatRv.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }

    private void callApi(String prompt) {

        messageList.add(new Message("typing.....",Message.SENT_BY_BOT));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model","gpt-3.5-turbo");

            JSONArray jsonArrayMsg = new JSONArray();
            JSONObject msgObj = new JSONObject();
            msgObj.put("role","user");
            msgObj.put("content",prompt);
            jsonArrayMsg.put(msgObj);

            jsonObject.put("messages", jsonArrayMsg);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("ERROR",e.getMessage().toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    JSONObject jsonResponseObject = null;
                    try {
                        jsonResponseObject = new JSONObject(response.body().string());
                        JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("choices");
                        String result = jsonResponseArray.getJSONObject(0).getJSONObject("message").getString("content");

                        addResponse(result.trim());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    Log.d("ERROR In Getting Response", response.toString());
                }
            }
        });
    }

    private void addResponse(String trim) {
        messageList.remove(messageList.size()-1);
        addToChatList(trim, Message.SENT_BY_BOT);
    }

    // attach menu as to actionBar as activity created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // your action here....
        if (item.getItemId() == R.id.delete_chats) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageList.clear();
                    adapter.notifyDataSetChanged();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}