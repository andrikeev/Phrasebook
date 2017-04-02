package ru.vandrikeev.android.phrasebook.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.ui.fragment.favorites.FavoritesFragment;
import ru.vandrikeev.android.phrasebook.ui.fragment.history.HistoryFragment;
import ru.vandrikeev.android.phrasebook.ui.fragment.translation.TranslationFragment;

/**
 * Main activity of application. Contains {@link BottomNavigationView} for navigation between three fragments:
 * <ul>
 * <li>{@link TranslationFragment} with translation function</li>
 * <li>{@link HistoryFragment} with translation history</li>
 * <li> {@link FavoritesFragment} with favorites translations</li>
 * </ul>
 * <p>
 * From Google MD guidelines
 * (<a href="https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior">
 * Bottom navigation behavior
 * </a>):
 * <p>
 * "Tapping on a bottom navigation icon takes you directly to the associated view, or refreshes the currently active
 * view.
 * <p>
 * On Android, the Back button does not navigate between bottom navigation bar views.
 * <p>
 * Navigation through the bottom navigation bar should reset the task state."
 * <p>
 * So, for {@link TranslationFragment} second click on tab icon will refresh translation screen, for others it will
 * scroll to the top of list.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final Fragment fragment = fragmentManager.findFragmentById(R.id.content);
            switch (item.getItemId()) {
                case R.id.navigation_translation:
                    // Just replace current fragment with new
                    fragmentManager.beginTransaction()
                            .replace(R.id.content, new TranslationFragment())
                            .commit();
                    return true;

                case R.id.navigation_history:
                    if (fragment instanceof HistoryFragment) {
                        // If current screen already contains history fragment just scroll to the top of list
                        final HistoryFragment historyFragment = (HistoryFragment) fragment;
                        historyFragment.scrollToTop();
                    } else {
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, new HistoryFragment())
                                .commit();
                    }
                    return true;
                case R.id.navigation_favorites:
                    if (fragment instanceof FavoritesFragment) {
                        // If current screen already contains favorites fragment just scroll to the top of list
                        final FavoritesFragment favoritesFragment = (FavoritesFragment) fragment;
                        favoritesFragment.scrollToTop();
                    } else {
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, new FavoritesFragment())
                                .commit();
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, new TranslationFragment())
                    .commit();
        }
    }
}
