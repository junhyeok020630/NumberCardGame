package org.techtown.hello;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 하단 패널 (진행 시간·최고 기록·Game Start 버튼)
 * - Game Start 클릭 시: onGameStart() 콜백 → 타이머 시작
 * - onGameFinished() : MainActivity 에서 호출 → 타이머 정지·최고 기록 검사
 */
public class SettingView extends LinearLayout {

    private TextView txtTimer, txtBest;
    private Button   btnStart;
    private Button btnReset;

    private long startMillis = 0;           // 시작 시각(ms)
    private long bestRecord  = Long.MAX_VALUE;
    private final Handler handler = new Handler();
    private Runnable tickTask;

    /* 외부(액티비티)로 알리는 인터페이스 */
    public interface GameControlListener { void onGameStart(); void onGameReset();}
    private GameControlListener listener;
    public void setGameControlListener(GameControlListener l) { listener = l; }

    public SettingView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(ctx); }
    public SettingView(Context ctx) { super(ctx); init(ctx); }

    private void init(Context ctx) {

        setOrientation(VERTICAL);
        LayoutInflater.from(ctx)
                .inflate(R.layout.setting_view, this, true);

        txtTimer = findViewById(R.id.txtTimer);
        txtBest  = findViewById(R.id.txtBest);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        btnStart.setOnClickListener(v -> {
            if (listener != null) listener.onGameStart();   // 외부 알림
            startMillis = System.currentTimeMillis();       // 타이머 시작
            runTimer();
            btnStart.setEnabled(false);                     // 버튼 잠금
        });

        btnReset.setOnClickListener(v -> {
            if (listener != null) listener.onGameReset();
        });
    }

    /* 1초마다 Time 갱신 */
    private void runTimer() {
        tickTask = () -> {
            long sec = (System.currentTimeMillis() - startMillis) / 1000;
            txtTimer.setText("Time  : " + sec + " s");
            handler.postDelayed(tickTask, 1000);
        };
        handler.post(tickTask);
    }

    /* 게임 완료(MainActivity) → 타이머 정지·베스트 검사 */
    public void onGameFinished() {
        handler.removeCallbacks(tickTask);          // 타이머 중지
        long record = (System.currentTimeMillis() - startMillis) / 1000;
        txtTimer.setText("Time  : " + record + " s");
        if (record < bestRecord) {                  // 최고 기록 갱신
            bestRecord = record;
            txtBest.setText("Best  : " + bestRecord + " s");
        }
        btnStart.setEnabled(true);                  // 다시 시작 가능
    }

    /* CustomView 에서 경과 시간 구할 때 사용 */
    public long getStartMillis() { return startMillis; }

    public void resetUI() {
        handler.removeCallbacks(tickTask);
        txtTimer.setText("Time  : 0 s");
        btnStart.setEnabled(true);
    }

    public void setBest(long sec){
        if(sec != Long.MAX_VALUE){
            bestRecord = sec;
            txtBest.setText("Best : "+bestRecord+ " s");
        }
    }
    /* +초 패널티 */
    public void addPenaltySeconds(int sec){
        startMillis -= sec * 1000L;     // 경과시간 늘리기
    }
    /* n초 정지 */
    public void pauseTimer(int sec){
        handler.removeCallbacks(tickTask);          // 타이머 멈춤
        handler.postDelayed(this::runTimer, sec*1000L);

        /* ★ stop 구간만큼 startMillis를 뒤로 미뤄준다 → 경과 시간 보정 */
        startMillis += sec * 1000L;
    }


}
