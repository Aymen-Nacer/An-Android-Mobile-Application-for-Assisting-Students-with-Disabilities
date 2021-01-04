package com.example.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.loadingview.LoadingView;

import java.io.IOException;
import java.util.List;

public class SpeechAdapter extends RecyclerView.Adapter<SpeechAdapter.MyViewHolder> {
    public List<Speech> list;
    private Activity activity;
    ProgressDialog mProgressDialog;
    String url;
    Handler handler;
    String sp;
    MyViewHolder viewHolder;

    public SpeechAdapter(List<Speech> itemsList, final Activity activity) {
        this.list = itemsList;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView answer, speak;
        public LinearLayout lin1, lin2;
        public RelativeLayout cardView;
        public LoadingView loadingView;

        public MyViewHolder(View view) {
            super(view);
            answer = view.findViewById(R.id.textspeech);
            speak = view.findViewById(R.id.textspeech2);
            lin1 = view.findViewById(R.id.lin1);
            lin2 = view.findViewById(R.id.lin2);
            cardView = view.findViewById(R.id.cardview);
            loadingView = view.findViewById(R.id.ActivityMediaPlayerLoadingView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_speech, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if (list.get(position).i == 1) {
            holder.answer.setText(list.get(position).text);
            holder.lin1.setVisibility(View.VISIBLE);
            holder.lin2.setVisibility(View.GONE);
            CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            holder.cardView.setGravity(Gravity.LEFT);
            holder.cardView.setLayoutParams(params);
        } else {
            CardView.LayoutParams params1 = new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
            params1.setMargins(10, 10, 10, 10);
            holder.cardView.setGravity(Gravity.RIGHT);
            holder.cardView.setLayoutParams(params1);
            holder.lin1.setVisibility(View.GONE);
            holder.lin2.setVisibility(View.VISIBLE);
            holder.speak.setText(list.get(position).text);
            handler = new Handler();
            holder.speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpeechAdapter.this.viewHolder = holder;
                    sp = holder.speak.getText().toString();
                    checkConnection();
                }
            });
        }
    }

    private class SpeechText extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("ارجوك انتظر");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            mProgressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                fetchJsonResponse(sp, viewHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void fetchJsonResponse(final String speech, final MyViewHolder holder) {
        AudioManager audioManager = (AudioManager) activity.getApplication().getSystemService(Context.AUDIO_SERVICE);
        url = "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=" + speech + "&tl=ar&client=tw-ob";
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkConnection() {
        if (isOnline()) {
            new SpeechText().execute();
        } else {
            Toast.makeText(activity, activity.getString(R.string.messagenet), Toast.LENGTH_SHORT).show();
        }
    }
}
