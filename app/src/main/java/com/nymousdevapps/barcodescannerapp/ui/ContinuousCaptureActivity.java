package com.nymousdevapps.barcodescannerapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication;
import com.nymousdevapps.barcodescannerapp.R;
import com.nymousdevapps.barcodescannerapp.Utils.BarcodeItem;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class ContinuousCaptureActivity extends Activity {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private String barcode;
    private int typeFragment;
    private Integer quantity = 0;
    private List<BarcodeItem> resultList = new ArrayList<>();
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null) { //|| result.getText().equals(lastText)
                // Prevent duplicate scans
                return;
            }

            if (typeFragment == 2) {
                if (!Objects.equals(result.getText(), barcode)) {
                    Toast.makeText(getApplicationContext(), "Error el codigo de barras escaneado no coincide con el del producto", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (typeFragment == 3) {
                if (result.getText().equals(lastText)) {
                    if (resultList.size() != 0) {
                        for (int i = 0; i < resultList.size(); i++) {
                            String productBar = resultList.get(i).getBarcode();
                            if (productBar.equals(result.getText())) {
                                int productQu = resultList.get(i).getQuantity();
                                productQu = productQu + 1;
                                resultList.set(i, new BarcodeItem(resultList.get(i).getBarcode(), productQu));
                            }
                        }
                    } else {
                        resultList.add(new BarcodeItem(result.getText(), 1));
                    }
                } else {
                    boolean foundProduct = false;
                    if (resultList.size() != 0) {
                        for (int i = 0; i < resultList.size(); i++) {
                            String productBar = resultList.get(i).getBarcode();
                            if (productBar.equals(result.getText())) {
                                int productQu = resultList.get(i).getQuantity();
                                productQu = productQu + 1;
                                resultList.set(i, new BarcodeItem(resultList.get(i).getBarcode(), productQu));
                                foundProduct = true;
                            }
                        }
                        if (!foundProduct) {
                            resultList.add(new BarcodeItem(result.getText(), 1));
                        }
                    } else {
                        resultList.add(new BarcodeItem(result.getText(), 1));
                    }
                }
            }

            final Handler handler = new Handler(Looper.getMainLooper());
                lastText = result.getText();
                barcodeView.setStatusText(result.getText());
                quantity = quantity +1;
                beepManager.playBeepSoundAndVibrate();
                barcodeView.pauseAndWait();

                handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    barcodeView.resume();
                }
            }, 1000);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_capture);

        Intent intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        typeFragment = intent.getIntExtra("typeFragmentScanner", 0);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.EAN_13, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    public void finish(View view) {
        if (typeFragment == 2) {
            Intent gotoMain = new Intent();
            gotoMain.putExtra("quantity", quantity);
            setResult(Activity.RESULT_OK, gotoMain);
            finish();
        } else if (typeFragment == 3) {
            Intent gotoMain = new Intent();
            Bundle args = new Bundle();
            args.putSerializable("scannerResultList", (Serializable) resultList);
            gotoMain.putExtra("bundle", args);
            setResult(Activity.RESULT_OK, gotoMain);
            finish();
        }
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void checkBarcodeResultForProductFragment(BarcodeResult result) {
        if(result.getText() == null) { //|| result.getText().equals(lastText)
            // Prevent duplicate scans
            return;
        }

        if (!Objects.equals(result.getText(), barcode)) {
            Toast.makeText(getApplicationContext(), "Error el codigo de barras escaneado no coincide con el del producto", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void checkBarcodeResultForCartFragment(BarcodeResult result) {
        if(result.getText() == null) { //|| result.getText().equals(lastText)
            // Prevent duplicate scans
                 return;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = BarcodeScannerAppApplication.Companion.getSharedPref().edit();
        editor.putInt("QuantityDetail", quantity);
        editor.apply();
    }
}