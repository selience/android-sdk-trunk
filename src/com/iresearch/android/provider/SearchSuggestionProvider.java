
package com.iresearch.android.provider;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    final static String AUTHORITY = "com.android.search.SearchSuggestionProvider";
    final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }

}
