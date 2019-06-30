package pw.komarov.webbrowser;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private WebView webView;
    private ProgressBar progressBar;
    private String currentUrl = "";
    private String newUrl = "";
    private MenuItem goStopRefreshItem;

    private enum CurrentAction {GO, STOP, REFRESH};
    private CurrentAction currentAction = CurrentAction.GO;

    private void setCurrentAction(CurrentAction currentAction) {
        this.currentAction = currentAction;
        switch (currentAction) {
            case GO:
                goStopRefreshItem.setIcon(R.drawable.ic_go_black_24dp);
                break;
            case STOP:
                goStopRefreshItem.setIcon(R.drawable.ic_stop_24dp);
                break;
            case REFRESH:
                goStopRefreshItem.setIcon(R.drawable.ic_refresh_24dp);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.pb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        goStopRefreshItem = menu.findItem(R.id.app_bar_go_stop_refresh);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_go_stop_refresh:
                switch (currentAction) {
                    case GO:
                        getPage(newUrl);
                        break;
                    case STOP:
                        webView.stopLoading();
                        break;
                    case REFRESH:
                        getPage(currentUrl);
                        break;
                }
                return true;
            case R.id.app_bar_back:
                goBack();
                return true;
            case R.id.app_bar_fwd:
                goForward();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getPage(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setCurrentAction(CurrentAction.GO);
        newUrl = newText;
        return true;
    }

    public boolean goBack() {
        if(webView.canGoBack()) {
            webView.goBack();
            return true;
        } else
            return false;
    }

    public boolean goForward() {
        if(webView.canGoForward()) {
            webView.goForward();
            return true;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {
        if(!goBack())
            super.onBackPressed();
    }

    protected void getPage(String url) {
        setCurrentAction(CurrentAction.STOP);
        currentUrl = url;
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
                }
            }
        });

        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
