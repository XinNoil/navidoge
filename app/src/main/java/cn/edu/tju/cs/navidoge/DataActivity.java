package cn.edu.tju.cs.navidoge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    public int Num=10;
    public Button[] buttons=new Button[Num];
    public TextView[] textViews=new TextView[Num];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setButtons();
        setViews();
        MyApp.getDataControl().setContext(this);
        MyApp.getDataControl().initWiFiScan();
        MyApp.getDataControl().getWiFiScan().OpenWifi();
        MyApp.getDataControl().timer();
        askPermission();
    }
    private void setButtons(){
        ButtonListener buttonListener=new ButtonListener();
        buttons[0]=findViewById(R.id.button1);
        buttons[1]=findViewById(R.id.button2);
        buttons[2]=findViewById(R.id.button3);
        buttons[3]=findViewById(R.id.button4);
        buttons[4]=findViewById(R.id.button_sensor);
        buttons[4].setText("ALL");
        buttons[5]=findViewById(R.id.button6);
        buttons[6]=findViewById(R.id.button7);
        buttons[7]=findViewById(R.id.button8);
        for (int i=0;i<Num;i++){
            if(buttons[i]!=null)
                buttons[i].setOnClickListener(buttonListener);
        }
    }
    public void setViews(){
        textViews[0]=findViewById(R.id.status_panel);
        textViews[1]=findViewById(R.id.debug_panel);
        MyApp.getDataControl().textViews=textViews;
    }
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.button_sensor:
                    buttons[4].setText(MyApp.getDataControl().changIndex());
            }
        }
    }
    private void askPermission(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(DataActivity.this, permissions, 1);
        }
        else{
            MyApp.getDataControl().getGpsScan().requestLocation();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        MyApp.getDataControl().getGpsScan().getmLocationClient().stop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    MyApp.getDataControl().getGpsScan().requestLocation();
                }
                else{
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
