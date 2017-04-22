package ru.vandrikeev.android.phrasebook;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.vandrikeev.android.phrasebook.di.AppComponent;
import ru.vandrikeev.android.phrasebook.di.AppModule;
import ru.vandrikeev.android.phrasebook.di.DaggerAppComponent;

/**
 * Real application class. Needed for DI with dagger2.
 */
public final class App extends Application {

    @NonNull
    @SuppressWarnings("NullableProblems")
    private AppComponent dependencyGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        dependencyGraph = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @NonNull
    public AppComponent getDependencyGraph() {
        return dependencyGraph;
    }
}
