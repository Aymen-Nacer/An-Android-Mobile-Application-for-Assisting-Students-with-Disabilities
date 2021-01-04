package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class Conversation extends AppCompatActivity {
    ArrayList<String> result = new ArrayList<>();
    private ImageButton mSendButton, mSpeakButton;
    EditText mEditText;
    SpeechAdapter mMessagesAdapter;
    RecyclerView mMessagesRecyclerView;
    ArrayList<Speech> mMessagesArrayList = new ArrayList<>();
    RelativeLayout mInputHolderRelativeLayout;
    int mMessagesCounter = 0, sp = 0;
    private SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    ImageView mGifImageView;
    ExpandableHeightGridView mSuggestionsExpandableHeightGridView;
    private ArrayList<String> mSuggestionsArrayList = new ArrayList<>();
    private ImageView mSuggestionsImageView;
    private boolean mSuggestionsShown = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        getSupportActionBar().hide();

        addSuggestionTexts();
        initializeViews();
        setViews();
        getKeyboardHeight();
        defineClickEvents();
        if (!isOnline()) {
            if (loadData().equals("En")) {
                Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "أنت غير متصل بالإنترنت", Toast.LENGTH_SHORT).show();
            }
        }
        if (!(ActivityCompat.checkSelfPermission(Conversation.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Conversation.this, new String[]{Manifest.permission.RECORD_AUDIO}, 110);
        }
    }



    private void setViews() {
        if (loadData().equals("En")) {
            mEditText.setHint("Please enter text");
        } else {
            mEditText.setHint("يرجى إدخال نص");
        }
    }

    //Method for initializing the texts that should be shown as suggestions
    private void addSuggestionTexts() {
        if (loadData().equals("En")) {
            mSuggestionsArrayList.add("Hi! I'm deaf. Help me please");
            mSuggestionsArrayList.add("I can't hear you. We can communicate via this app");
            mSuggestionsArrayList.add("Speak via the microphone to communicate with me. I'n deaf.");
            mSuggestionsArrayList.add("Hi! How are you?");
            mSuggestionsArrayList.add("Bye!");
            mSuggestionsArrayList.add("Thank you!");
            mSuggestionsArrayList.add("Could you help me?");
            mSuggestionsArrayList.add("What's your name?");
            mSuggestionsArrayList.add("Give me your phone number please.");
            mSuggestionsArrayList.add("Show me the right way please.");
        } else {
            mSuggestionsArrayList.add("مرحبا! أنا أصم ، ساعدني من فضلك.");
            mSuggestionsArrayList.add("لا أستطيع سماعك. يمكننا التواصل عبر هذا التطبيق");
            mSuggestionsArrayList.add("تحدث عبر الميكروفون للتواصل معي. انا أصم");
            mSuggestionsArrayList.add("السلام عليكم كيف حالك؟");
            mSuggestionsArrayList.add("وعليكم السلام ورحمة الله وبركاته");
            mSuggestionsArrayList.add("شكراً لك");
            mSuggestionsArrayList.add("هل تستطيع مساعدتي؟");
            mSuggestionsArrayList.add("ماهو اسمك؟");
            mSuggestionsArrayList.add("اعطني رقم هاتفك لو سمحت");
            mSuggestionsArrayList.add("مع السلامة");
            mSuggestionsArrayList.add("أرِني الطريق من فضلك");

        }
    }

    public void startSpeechRecognition() {
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                mSpeakButton.setClickable(false);
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                //Getting speech recognition result and displaying it
                result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                assert result != null;
                if (!result.isEmpty() && sp == 1) {
                    mEditText.setText(result.get(0));
                    Speech speech = new Speech(mEditText.getText().toString(), 1);
                    mMessagesArrayList.add(speech);
                    mMessagesAdapter = new SpeechAdapter(mMessagesArrayList, Conversation.this);
                    mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(Conversation.this));
                    mMessagesRecyclerView.setAdapter(mMessagesAdapter);
                    mMessagesAdapter.notifyItemInserted(mMessagesArrayList.size() - 1);
                    mMessagesRecyclerView.scrollToPosition(mMessagesArrayList.size() - 1);
                    result.clear();
                    mEditText.setText(null);
                    sp = 0;
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void defineClickEvents() {
        mSuggestionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSuggestions();
            }
        });
        //Adding a new message to list
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mEditText.getText().toString();
                if (!ip.trim().equals("")) {
                    Speech speech = new Speech(ip, 2);
                    mMessagesArrayList.add(speech);
                    mMessagesAdapter = new SpeechAdapter(mMessagesArrayList, Conversation.this);
                    mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(Conversation.this));
                    mMessagesCounter = mMessagesCounter + 1;
                    mMessagesRecyclerView.setAdapter(mMessagesAdapter);
                    mMessagesRecyclerView.smoothScrollToPosition(mMessagesCounter);
                    mEditText.setText(null);
                }
            }
        });
        //Listening for user voice and setting relevant UI
        mSpeakButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isOnline()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            mSpeakButton.setImageResource(R.drawable.ic_microphone);
                            mEditText.setVisibility(View.VISIBLE);
                            mGifImageView.setVisibility(View.GONE);
                            mSpeechRecognizer.stopListening();
                            sp = 1;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            sp = 0;
                            Glide.with(Conversation.this).load(R.drawable.speaker).into(mGifImageView);
                            mSpeakButton.setImageResource(R.drawable.ic_microphone1);
                            mEditText.setVisibility(View.GONE);
                            mGifImageView.setVisibility(View.VISIBLE);
                            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                            startSpeechRecognition();
                            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            if (loadData().equals("En")) {
                                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
                            } else {
                                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar_SA");
                            }
                            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                            break;
                    }
                } else {
                    if (loadData().equals("En")) {
                        Toast.makeText(Conversation.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Conversation.this, "يرجى الاتصال بالإنترنت", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //Changing the send button icon depending on whether text field is empty or not
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditText.getText().toString().isEmpty()) {
                    mSpeakButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                    params.addRule(RelativeLayout.START_OF, R.id.ImgSpeak);
                } else {
                    mSpeakButton.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                    params.addRule(RelativeLayout.START_OF, R.id.btnsend);
                }
                params.addRule(RelativeLayout.END_OF, R.id.suggestions_button);
                mInputHolderRelativeLayout.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initializeViews() {
        mSendButton = findViewById(R.id.btnsend);
        mEditText = findViewById(R.id.EditText);
        mMessagesRecyclerView = findViewById(R.id.rec);
        mSpeakButton = findViewById(R.id.ImgSpeak);
        mInputHolderRelativeLayout = findViewById(R.id.Rel1);
        mGifImageView = findViewById(R.id.voice);
        mSuggestionsExpandableHeightGridView = findViewById(R.id.suggestions_grid_view);
        mSuggestionsImageView = findViewById(R.id.suggestions_button);
    }

    //Checking user network status
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //Closing suggestions tab if it is open
    @Override
    public void onBackPressed() {
        if (mSuggestionsShown) {
            mSuggestionsExpandableHeightGridView.setVisibility(View.GONE);
            mSuggestionsShown = false;
        } else {
            super.onBackPressed();
        }
    }

    //Opening/Closing suggestions tab when the button is pressed and defining actions that need to be taken when one suggestions is clicked
    private void openSuggestions() {
        if (!mSuggestionsShown) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences("SharedPreference", MODE_PRIVATE);
                    int keyboardHeight = sharedPreferences.getInt("KeyboardHeight", 0);
                    mSuggestionsExpandableHeightGridView.setVisibility(View.VISIBLE);
                    mSuggestionsExpandableHeightGridView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight));
                    mSuggestionsExpandableHeightGridView.setAdapter(new SuggestionsAdapter(Conversation.this, R.layout.suggestions_template, mSuggestionsArrayList));
                    mSuggestionsShown = true;
                    mSuggestionsExpandableHeightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Speech speech = new Speech(mSuggestionsArrayList.get(position), 2);
                            mMessagesArrayList.add(speech);
                            mMessagesAdapter = new SpeechAdapter(mMessagesArrayList, Conversation.this);
                            mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(Conversation.this));
                            mMessagesCounter = mMessagesCounter + 1;
                            mMessagesRecyclerView.setAdapter(mMessagesAdapter);
                            mMessagesRecyclerView.smoothScrollToPosition(mMessagesCounter);
                            mEditText.setText(null);
                        }
                    });
                }
            }, 300);
        } else {
            mSuggestionsExpandableHeightGridView.setVisibility(View.GONE);
            mSuggestionsShown = false;
        }
    }

    //Getting height of soft keyboard so the height of suggestions tab is the same. Used only once after installation.
    private void getKeyboardHeight() {
        final View myLayout = getWindow().getDecorView().getRootView();
        myLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                myLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreference", MODE_PRIVATE);
                int height = sharedPreferences.getInt("KeyboardHeight", 0);
                if (heightDifference > 150) {
                    mSuggestionsExpandableHeightGridView.setVisibility(View.GONE);
                    mSuggestionsShown = false;
                }
                if (heightDifference > height && heightDifference > 150) {
                    sharedPreferences.edit().putInt("KeyboardHeight", heightDifference).apply();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreference", MODE_PRIVATE);
        int height = sharedPreferences.getInt("KeyboardHeight", 0);
        if (height == 0) {
            if (mEditText.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }

    //Forced showing of soft keyboard so its height can be measured.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreference", MODE_PRIVATE);
        int height = sharedPreferences.getInt("KeyboardHeight", 0);
        if (height == 0) {
            if (mEditText.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
}