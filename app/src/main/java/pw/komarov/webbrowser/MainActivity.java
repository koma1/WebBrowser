package pw.komarov.webbrowser;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private ImageButton btnGoStopRefresh;
    private ImageButton btnBack;
    private ImageButton btnForward;
    private EditText edtUrl;
    private boolean needUrlRefresh = false;

    private enum CurrentAction {GO, STOP, REFRESH};
    private CurrentAction currentAction = CurrentAction.GO;

    private void setCurrentAction(CurrentAction currentAction) {
        this.currentAction = currentAction;
        switch (currentAction) {
            case GO:
                btnGoStopRefresh.setImageResource(R.drawable.ic_action_go_24dp);
                break;
            case STOP:
                btnGoStopRefresh.setImageResource(R.drawable.ic_action_stop_24dp);
                break;
            case REFRESH:
                btnGoStopRefresh.setImageResource(R.drawable.ic_action_refresh_24dp);
                break;
        }

    }

    private void initContent() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.pb);
        btnGoStopRefresh = findViewById(R.id.btnGoStopRefresh);
        edtUrl = findViewById(R.id.edtUrl);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        final EditText edtUrl = findViewById(R.id.edtUrl);

        edtUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                goUrl(v.getText().toString());
                return true;
            }
        });
        edtUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setCurrentAction(CurrentAction.GO);
                return true;
            }
        });

        ImageButton btnGoStopRefresh = findViewById(R.id.btnGoStopRefresh);
        btnGoStopRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentAction) {
                    case GO:
                        goUrl(edtUrl.getText().toString());
                        break;
                    case STOP:
                        webView.stopLoading();
                        break;
                    case REFRESH:
                        goUrl(edtUrl.getText().toString());
                        break;
                }
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForward();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContent();
    }

    public boolean goBack() {
        if(webView.canGoBack()) {
            webView.goBack();
            needUrlRefresh = true;

            return true;
        } else
            return false;
    }

    public boolean goForward() {
        if(webView.canGoForward()) {
            webView.goForward();
            needUrlRefresh = true;

            return true;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {
        if(!goBack())
            super.onBackPressed();
    }

    public void refreshNavigationState() {
        if(needUrlRefresh) {
            edtUrl.setText(webView.getUrl());
            needUrlRefresh = false;
        }

        btnForward.setImageResource((webView.canGoForward())
                ? R.drawable.ic_navigate_forward_24dp
                    : R.drawable.ic_navigate_forward_disabled_24dp);

        btnBack.setImageResource((webView.canGoBack())
                ? R.drawable.ic_navigate_back_24dp
                    : R.drawable.ic_navigate_back_disabled_24dp);
    }

    protected void goUrl(String url) {
        if((!url.toLowerCase().startsWith("http://")) && (!url.toLowerCase().startsWith("https://")))
            url = "https://" + url;
        webView.requestFocus();
        setCurrentAction(CurrentAction.STOP);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress){
                progressBar.setProgress(newProgress);
                if(newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    setCurrentAction(CurrentAction.REFRESH);
                    refreshNavigationState();
                }
            }
        });

        webView.loadUrl(url);
    }
}
