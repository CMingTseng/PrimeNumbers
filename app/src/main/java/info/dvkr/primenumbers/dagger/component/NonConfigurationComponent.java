package info.dvkr.primenumbers.dagger.component;


import dagger.Subcomponent;
import info.dvkr.primenumbers.background.BackgroundService;
import info.dvkr.primenumbers.dagger.PersistentScope;
import info.dvkr.primenumbers.view.MainActivity.MainActivity;

@PersistentScope
@Subcomponent
public interface NonConfigurationComponent {

    void inject(MainActivity mainActivity);

    void inject(BackgroundService service);
}
