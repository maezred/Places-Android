package net.moltendorf.places;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "handleIntent: Performing search for \"" + query + "\"");
		}
	}
}
