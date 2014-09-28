
package com.iresearch.android;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import org.mariotaku.android.activity.BaseActionBarActivity;
import org.mariotaku.android.utils.Toaster;

public class SearchActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }

    @Override
    protected int layout() {
        return 0;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toaster.show(this, "搜索结果：" + query);
        }
    }

}
