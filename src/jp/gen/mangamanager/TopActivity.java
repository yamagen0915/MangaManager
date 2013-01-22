package jp.gen.mangamanager;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TopActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        
        TabHost host = getTabHost();
        host.setup();
        TabSpec tab1 = host.newTabSpec("tab1");
        tab1.setIndicator("É}ÉìÉK");
        tab1.setContent(new Intent(this,Novels.class));
        host.addTab(tab1);
        
        TabSpec tab2 = host.newTabSpec("tab2");
        tab2.setIndicator("ÉAÉjÉÅ");
        tab2.setContent(new Intent(this,Animes.class));
        host.addTab(tab2);
    }
}
