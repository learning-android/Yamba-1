package com.marakana.android.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;

public class StatusActivity extends Activity {
	private static final String TAG = StatusActivity.class.getSimpleName();
	private Button mButtonTweet;
	private EditText mTextStatus;
	private TextView mTextCount;
	private int mDefaultColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		mButtonTweet = (Button) findViewById(R.id.status_button_tweet);
		mTextStatus = (EditText) findViewById(R.id.status_text);
		mTextCount = (TextView) findViewById(R.id.status_text_count);
		mTextCount.setText(Integer.toString(140));
		mDefaultColor = mTextCount.getTextColors().getDefaultColor();

		mButtonTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String status = mTextStatus.getText().toString();
				PostTask postTask = new PostTask();
				postTask.execute(status);
				Log.d(TAG, "onClicked");
			}

		});

		mTextStatus.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				int count = 140 - s.length();
				mTextCount.setText(Integer.toString(count));

				if (count < 50) {
					mTextCount.setTextColor(Color.RED);
				} else {
					mTextCount.setTextColor(mDefaultColor);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		Log.d(TAG, "onCreated");
	}

	class PostTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(StatusActivity.this, "Posting",
					"Please wait...");
			progress.setCancelable(true);
		}

		// Executes on a non-UI thread
		@Override
		protected String doInBackground(String... params) {
			try {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(StatusActivity.this);
				String username = prefs.getString("username", "");
				String password = prefs.getString("password", "");

				YambaClient cloud = new YambaClient(username, password);
				cloud.postStatus(params[0]);

				Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
				return "Successfully posted";
			} catch (Exception e) {
				Log.e(TAG, "Failed to post to the cloud", e);
				e.printStackTrace();
				return "Failed to post";
			}
		}

		// Called after doInBackground() on UI thread
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			if (this != null && result != null)
				Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
						.show();
		}

	}

}
