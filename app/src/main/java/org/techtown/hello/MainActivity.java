package org.techtown.hello;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private CustomView  customView;
    private SettingView settingView;

    private SharedPreferences pref;
    private static final String KEY_BEST = "BEST_SEC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("record", MODE_PRIVATE);

        customView  = findViewById(R.id.customView);
        settingView = findViewById(R.id.settingView);

        customView.setSettingView(settingView);

        /* 저장된 최고 기록을 SettingView 에 주입 */
        long best = pref.getLong(KEY_BEST, Long.MAX_VALUE);
        settingView.setBest(best);

        settingView.setGameControlListener(new SettingView.GameControlListener() {
            @Override public void onGameStart() {
                customView.setGameStarted(true);
                Toast.makeText(MainActivity.this,"게임 시작!\n 숫자 쌍을 순서대로 찾아주세요!",Toast.LENGTH_SHORT).show();
            }
            @Override public void onGameReset() {
                customView.resetBoard();             // 1) 보드 리셋
                settingView.resetUI();               // 2) 타이머·버튼 초기화
                Toast.makeText(MainActivity.this,"게임 리셋!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* CustomView 에서 모든 숫자를 맞췄을 때 호출 */
    public void onGameFinished(long elapsedSec){
        long prevBest = pref.getLong(KEY_BEST, Long.MAX_VALUE);
        boolean isNew = elapsedSec < prevBest;

        if(isNew){
            pref.edit().putLong(KEY_BEST, elapsedSec).apply();
            settingView.setBest(elapsedSec);          // UI 즉시 반영
        }

        settingView.onGameFinished();
        customView.setGameStarted(false);

        GameOverDialogFragment.newInstance(elapsedSec,
                        isNew ? elapsedSec : prevBest,
                        isNew)                          // ← boolean 추가
                .show(getSupportFragmentManager(),"game_over");
    }

    /* 다크/라이트 토글 메뉴 */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu); return true;
    }
    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            int mode = AppCompatDelegate.getDefaultNightMode();
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "라이트 모드!", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "다크 모드!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
