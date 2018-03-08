package cn.edu.tju.cs.navidoge;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.SearchView;

import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.SVGMapViewListener;

import cn.edu.tju.cs.navidoge.UI.AssetsHelper;

public class DemoActivity extends AppCompatActivity {
    private static final String TAG="DemoActivity";
    private static SVGMapView mapView;
    private static IndoorLocationService.IndoorLocationBinder mBinder;
    private static MHandler mHandler=new MHandler();
    private ServiceConnection connection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder= (IndoorLocationService.IndoorLocationBinder) iBinder;
            mBinder.setMessenger(mHandler);
            mBinder.initDataControl(DemoActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    static class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MyApp.toastText("Servicing");
                    break;
                case 2:
                    String locationString=msg.getData().getString("Location");
                    //MyApp.toastText(locationString);
                    float[] location=MyApp.getGson().fromJson(locationString,float[].class);
                    MyApp.toastText(String.valueOf(location[0])+" "+String.valueOf(location[1]));
                    location[0]=(location[0]+1.34f)*3.52f;
                    location[1]=(location[1]+7.5f)*3.52f;
                    mapView.setLocationOverlay(location);
                    mapView.refresh();
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_demo);

        SearchView mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setFocusable(false);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Intent intent=new Intent(DemoActivity.this,SearchActivity.class);
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinder.getLocation();
            }
        });

        initSVGMapView();
        mapView.loadMap(AssetsHelper.getContent(this, "55_5.svg"));

        CoordinatorLayout coordinatorLayout=findViewById(R.id.coordinatorLayout);
        Intent bindIntent = new Intent(DemoActivity.this, IndoorLocationService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        Snackbar.make(coordinatorLayout , "Indoor location service start.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void initSVGMapView(){
        mapView = (SVGMapView) findViewById(R.id.mapView);
        mapView.registerMapViewListener(new SVGMapViewListener() {
            @Override
            public void onMapLoadComplete() {
                DemoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoActivity.this, "onMapLoadComplete", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onMapLoadError() {
                DemoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoActivity.this, "onMapLoadError", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onGetCurrentMap(Bitmap bitmap) {
                DemoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoActivity.this, "onGetCurrentMap", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onClick(MotionEvent event) {
//                float XY[] = mapView.getMapCoordinateWithScreenCoordinate(event.getX(), event.getY());
//                MyApp.toastText("onClick: \n"
//                        + "On Screen{ x=" + String.valueOf(event.getX()) + " y=" + String.valueOf(event.getY()) + " } \n"
//                        + "On Map{ x=" + String.valueOf(XY[0]) + " y=" + String.valueOf(XY[1]) + " } ");
//                mapView.setLocationOverlay(XY);
//                mapView.refresh();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        unbindService(connection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_net_test:
                break;
            case R.id.action_send_message:
                mBinder.sendMessage();
                break;
        }
        return true;
    }
}