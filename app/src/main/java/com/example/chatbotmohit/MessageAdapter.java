package com.example.chatbotmohit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout,parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message messageData = messageList.get(position);
        if (messageData.sentBy.equals(Message.SENT_BY_ME)){
            //sent by user
            holder.linearChatBot.setVisibility(View.GONE);
            holder.linearChatHuman.setVisibility(View.VISIBLE);
            holder.humanText.setText(messageData.getMessage());

        }
        else {
            //sent by bot
            holder.linearChatHuman.setVisibility(View.GONE);
            holder.linearChatBot.setVisibility(View.VISIBLE);
            holder.botText.setText(messageData.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearChatBot,linearChatHuman;
        TextView botText,humanText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linearChatBot = itemView.findViewById(R.id.linearChatBot);
            linearChatHuman = itemView.findViewById(R.id.linearChatHuman);
            botText = itemView.findViewById(R.id.botText);
            humanText = itemView.findViewById(R.id.humanText);

        }
    }
}
