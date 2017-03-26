package ru.vandrikeev.android.phrasebook.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.ui.fragment.translation.TranslationFragment;

/**
 * Main activity of application. Contains {@link BottomNavigationView} for navigation between three fragments:
 * <ul>
 * <li>{@link TranslationFragment} with translation function</li>
 * <li>HistoryFragment with translation history</li>
 * <li>FavoritesFragment with favorites translations</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_translation:
                    if (fragmentManager.findFragmentById(R.id.content) instanceof TranslationFragment) {
                        return false;
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.content, new TranslationFragment())
                            .commit();
                    return true;

                case R.id.navigation_history:
                    //TODO add history fragment
                    Toast.makeText(MainActivity.this, "History tab selected", Toast.LENGTH_SHORT).show();
                    /*if (fragmentManager.findFragmentById(R.id.content) instanceof HistoryFragment) {
                        return false;
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.content, new HistoryFragment())
                            .commit();*/
                    return true;

                case R.id.navigation_favorites:
                    //TODO add favorites fragment
                    Toast.makeText(MainActivity.this, "Favorites tab selected", Toast.LENGTH_SHORT).show();
                    /*if (fragmentManager.findFragmentById(R.id.content) instanceof FavoritesFragment) {
                        return false;
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.content, new FavoritesFragment())
                            .commit();*/
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
