package org.techtown.hello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CustomView extends View {

    private int[] location4Image; // 각 셀에 실제로 배치된 숫자
    private int[] imgs; // 이미지 리소스 배열
    private Drawable[] drawable; // draw() 재사용을 위한 Drawble 캐시
    private Vector isCorrected; // 이미 짝을 맞춘 셀 인덱스 저장
    private boolean isShow; // 현재 고른 이미지가 4초간 공개 중인지? 변수
    private int answer; // 지금 맞춰야 할 숫자 (1~9)
    private Date d1; // 마지막으로 카드를 연 시간 저장
    private int width; // 화면 폭
    private int height; // 화면 높이

    /* 짝 맞추기용 변수 */
    private int revealIdx = -1; // 4초간 보여 주는 카드의 셀 인덱스
    private int firstPick = -1; // 해당 숫자의 '첫 번째 선택' 카드 인덱스

    /* 게임 시작 전 터치 차단 */
    private boolean isGameStarted = false;
    public void setGameStarted(boolean started) {isGameStarted = started; }

    /* 외부 SettingView의 시작 시각을 받아올 참조 */
    private SettingView settingView; // setSettingView()로 주입
    public void setSettingView(SettingView sv) {this.settingView = sv;}

    /* 능력 타입 상수 */
    private static final int AB_BOMB   = 0;   // 폭탄
    private static final int AB_STOP   = 1;   // 타임스탑
    private static final int AB_ALLSEE = 2;   // 사륜안

    /* 능력 3칸 인덱스 & 사용 여부 */
    private final int[] abilityIdx   = new int[3];   // 0~23
    private final boolean[] abilityUsed = {false,false,false};

    /* 전체 공개 상태 */
    private boolean isShowAll   = false;
    private long    allSeeStart = 0;


    private static MediaPlayer mediaPlayer;

    public CustomView(Context context, int w, int h) {
        super(context);
        this.width = w;
        this.height = h;
        init(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.width = context.getResources().getDisplayMetrics().widthPixels;
        this.height = context.getResources().getDisplayMetrics().heightPixels;
        init(context);
    }

    private void init(Context context) {

        /* 배열 컨테이너 초기화 */
        location4Image = new int[24]; // 4 * 6격자
        imgs = new int[10]; // 0~9 이미지
        drawable = new Drawable[24];
        isCorrected = new Vector(); // 이미 맞춘 카드 저장

        // 숫자 이미지 배열에 초기화
        imgs[0] = R.drawable.num_0;
        imgs[1] = R.drawable.num_1;
        imgs[2] = R.drawable.num_2;
        imgs[3] = R.drawable.num_3;
        imgs[4] = R.drawable.num_4;
        imgs[5] = R.drawable.num_5;
        imgs[6] = R.drawable.num_6;
        imgs[7] = R.drawable.num_7;
        imgs[8] = R.drawable.num_8;
        imgs[9] = R.drawable.num_9;


        /* 1. 모든 칸을 가림막(0번)으로 채우기 */
        for (int i = 0; i < location4Image.length; i++)
            location4Image[i] = imgs[0];

        /* 2. 1~9 숫자를 각 2장씩 무작위 위치에 배치 */
        int[] loc = getRandomLocation(); // 길이 18의 난수 위치 배열 반환
        // isCorrected.add(loc[0]);  isCorrected.add(loc[2]); // 구체적으로 구현 필요

        for (int i = 0; i < loc.length; i++) {
            int num = i / 2 + 1;    // i 0,1 -> 1 / 2,3 -> 2
            location4Image[loc[i]] = imgs[num]; // 실제 이미지 id 저장
        }

        /* 섞기 완료 후 0인 칸 6개 목록 구함 */
        List<Integer> zeros = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            if (location4Image[i] == imgs[0]) zeros.add(i);

        /* 무작위로 3칸 뽑아 능력 순서대로 할당 */
        Collections.shuffle(zeros);
        for (int k=0;k<3;k++){
            abilityIdx[k]   = zeros.get(k);   // 0:폭탄 1:스탑 2:사륜안
            abilityUsed[k]  = false;
        }


        /* 5. 게임 상태 초기화 */
        isShow = false;     // 처음엔 아무 카드도 공개 X
        answer = 1;         // 먼저 1 두 장을 찾아야 한다.
        d1 = new Date();    // 타이머 초기화

        /* 6. 화면 갱신용 스레드 시작 */
        BackgroundThread thread = new BackgroundThread();
        thread.start();
    }

    /* 18개의 랜덤 위치 생성 - 중복 X*/
    private int[] getRandomLocation() {
        int[] n = new int[18]; // 1~9까지 각 2장씩
        int index = 0;
        for(int i = 0; i < n.length; i++) {
            do {
                index = (int)(Math.random() * 24);  // 0~23중 난수 -> 격자 위치
            } while (exists(n, index));             // 중복이면 다시 뽑기
            n[i] = index;
        }
        return n;
    }

    /* 이미 뽑힌 위치인지 검사 */
    private boolean exists(int n[], int index) {
        for (int i = 0; i < n.length; i++) {
            if(n[i] == index)
                return true;
        }
        return false;
    }

    /* 그림 그리기 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* ── 셀 크기 & 여백 계산 ───────────────────────────── */
        int px   = Math.min(getWidth() / 4,   // 가로를 4등분
                getHeight() / 7); // 세로를 7등분(6행+위아래 여백)
        int sx   = px / 2;                    // 위쪽(아래도 동일) 여백 유지
        int offX = (getWidth() - px * 4) / 2; // 좌우 가운데 정렬용 추가 여백

        /* ── 이하 기존 로직(조건·루프) 그대로 ───────────────── */
        if (isShow) {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 6; j++) {
                    int idx   = j * 4 + i;

                    boolean showCard = isCorrected.contains(idx) || idx == firstPick
                            || (isShow && idx == revealIdx)
                            || isShowAll;                 // ★ 사륜안 활성화 시 모두 공개

                    int resId = showCard ? location4Image[idx] : imgs[0];

                    /* 루프 내부 — 셀 하나 그리기 직전에 */
                    /* 능력 칸이면 배경색 → ‘이미 사용했을 때’만 표시 */
                    boolean drawAbilityColor = (idx == abilityIdx[AB_BOMB ] && abilityUsed[AB_BOMB ])
                            || (idx == abilityIdx[AB_STOP ] && abilityUsed[AB_STOP ])
                            || (idx == abilityIdx[AB_ALLSEE] && abilityUsed[AB_ALLSEE]);

                    if (drawAbilityColor) {
                        Paint p = new Paint();
                        p.setColor( (idx == abilityIdx[AB_BOMB]) ? Color.RED : Color.BLUE );
                        p.setAlpha(80);
                        canvas.drawRect(offX+px*i, sx+px*j,
                                offX+px*(i+1), sx+px*(j+1), p);
                    }



                    drawable[idx] = getResources().getDrawable(resId);
                    drawable[idx].setBounds(
                            offX + px * i + 2, sx + px * j + 2,
                            offX + px * (i + 1) - 2, sx + px * (j + 1) - 2);
                    drawable[idx].draw(canvas);
                }
        } else {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 6; j++) {
                    int idx   = j * 4 + i;


                    boolean showCard = isCorrected.contains(idx) || isShowAll;
                    int resId = showCard ? location4Image[idx] : imgs[0];

                    /* 루프 내부 — 셀 하나 그리기 직전에 */
                    /* 능력 칸이면 배경색 → ‘이미 사용했을 때’만 표시 */
                    boolean drawAbilityColor = (idx == abilityIdx[AB_BOMB ] && abilityUsed[AB_BOMB ])
                            || (idx == abilityIdx[AB_STOP ] && abilityUsed[AB_STOP ])
                            || (idx == abilityIdx[AB_ALLSEE] && abilityUsed[AB_ALLSEE]);

                    if (drawAbilityColor) {
                        Paint p = new Paint();
                        p.setColor( (idx == abilityIdx[AB_BOMB]) ? Color.RED : Color.BLUE );
                        p.setAlpha(80);
                        canvas.drawRect(offX+px*i, sx+px*j,
                                offX+px*(i+1), sx+px*(j+1), p);
                    }


                    drawable[idx] = getResources().getDrawable(resId);
                    drawable[idx].setBounds(
                            offX + px * i + 2, sx + px * j + 2,
                            offX + px * (i + 1) - 2, sx + px * (j + 1) - 2);
                    drawable[idx].draw(canvas);
                }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isGameStarted) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            /* ── 1) 터치 좌표 → 셀(col,row) 계산 ───────────────────── */
            int px   = Math.min(getWidth() / 4, getHeight() / 7);
            int sx   = px / 2;
            int offX = (getWidth() - px * 4) / 2;

            /* ★ 그리드 실제 시작점 */
            int startX = offX + 2;        // onDraw()의 2px 여백과 일치
            int startY = sx   + 2;

            float x = event.getX();
            float y = event.getY();

            /* 1) 그리드 밖 터치면 무시 */
            if (x < startX || x > startX + px * 4 ||
                    y < startY || y > startY + px * 6)
                return true;

            /* 2) 칸 계산도 동일 기준 사용 */
            int col = (int) ((x - startX) / px);   // 0‥3
            int row = (int) ((y - startY) / px);   // 0‥5
            int idx = row * 4 + col;

            /* ── 3) 4초 공개 상태 갱신 ──────────────────────────── */
            revealIdx = idx;              // 이번에 열린 카드 인덱스
            isShow    = true;             // 4초 동안 공개
            d1        = new Date();       // 공개 시작 시각 기록
            /* ── 4) 클릭한 카드의 실제 숫자 값 추출 ─────────────── */
            int numClicked = getNumFromRes(location4Image[idx]);

            /* 능력 칸인지 검사 */
            int abType = -1;
            if (idx == abilityIdx[AB_BOMB])   abType = AB_BOMB;
            else if (idx == abilityIdx[AB_STOP])   abType = AB_STOP;
            else if (idx == abilityIdx[AB_ALLSEE]) abType = AB_ALLSEE;

            /* 능력 칸이고 아직 안 썼으면 발동 */
            if (abType != -1 && !abilityUsed[abType]) {
                abilityUsed[abType] = true;     // 한번만 사용

                switch (abType) {
                    case AB_BOMB:
                        settingView.addPenaltySeconds(5);
                        Toast.makeText(getContext(),"💣 +5초!",Toast.LENGTH_SHORT).show();
                        break;
                    case AB_STOP:
                        settingView.pauseTimer(5);
                        Toast.makeText(getContext(),"⏸ 아이스 에이지!",Toast.LENGTH_SHORT).show();
                        break;
                    case AB_ALLSEE:
                        isShowAll   = true;
                        allSeeStart = System.currentTimeMillis();
                        Toast.makeText(getContext(),"👁 사륜안 발동!",Toast.LENGTH_SHORT).show();
                        break;
                }
                invalidate();
                return true;           // 숫자 맞추기 로직은 스킵
            }

            /* ── 5) 정답 여부 판정 ──────────────────────────────── */
            if (numClicked == answer) {                 // ✔︎ 정답

                if (firstPick == -1) {                  // ① 첫 번째 장
                    firstPick = idx;
                    Log.d("DEBUG", "RIGHT (first) num=" + numClicked);
                    playSound(R.raw.rightact_1);

                } else {                                // ② 두 번째 장
                    if (firstPick != idx) {             // 같은 칸 두 번 눌림 방지
                        isCorrected.add(firstPick);
                        isCorrected.add(idx);
                        firstPick = -1;                 // 다음 숫자 준비
                        Log.d("DEBUG", "RIGHT (pair) num=" + numClicked);
                        playSound(R.raw.rightact_1);

                        answer++;                       // 1→2→…→9
                        if (answer > 9) {
                            long elapsedSec = (System.currentTimeMillis() - settingView.getStartMillis())/1000;
                            ((MainActivity)getContext()).onGameFinished(elapsedSec);
                        }
                    }
                }

            } else {                                   // ✘ 오답
                firstPick = -1;                        // 첫 장 초기화
                Log.d("DEBUG", "WRONG clicked=" + numClicked + " expect=" + answer);
                playSound(R.raw.wrong_1);
            }

            /* ── 7) 화면 다시 그리기 ───────────────────────────── */
            invalidate();
        }

        return true;   // 이벤트 소비
    }


    /* resId → 0~9 숫자값 반환 */
    private int getNumFromRes(int resId) {
        for (int i = 0; i < imgs.length; i++)
            if (imgs[i] == resId) return i;
        return -1;
    }

    class BackgroundThread extends Thread {
        long diff;
        long sec;

        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
                if (isShow) {
                    Date d2 = new Date();
                    diff = d2.getTime() - d1.getTime();
                    sec = diff / 1000;

                    if (sec >= 4) {
                        isShow = false;
                        revealIdx = -1;
                    }
                }
                if (isShowAll) {
                    if (System.currentTimeMillis() - allSeeStart >= 2000) {
                        isShowAll = false;          // 2초 지나면 해제
                    }
                    /* ★ 2초 동안 계속 화면을 새로 그리도록 한다 */
                    postInvalidate();
                    continue;                       // 아래 일반 로직으로 안 내려감
                }

                invalidate();
            }
        }
    }

    /* ------------------------------- ⑦ 효과음 ------------------------------- */
    private void playSound(int resId) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getContext(), resId);
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("Sound", "효과음 오류 : " + e.getMessage());
        }
    }

    /** 모든 상태를 초기화하고 새로 섞음 */
    public void resetBoard() {
        isCorrected.clear();
        firstPick = -1; answer = 1;
        isShow = false; revealIdx = -1;

        int[] loc = getRandomLocation();        // 새 섞기
        for (int i=0;i<24;i++) location4Image[i] = imgs[0];
        for (int k=0;k<loc.length;k++)
            location4Image[loc[k]] = imgs[k/2+1];

        /* 섞기 완료 후 0인 칸 6개 목록 구함 */
        List<Integer> zeros = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            if (location4Image[i] == imgs[0]) zeros.add(i);

        /* 무작위로 3칸 뽑아 능력 순서대로 할당 */
        Collections.shuffle(zeros);
        for (int k=0;k<3;k++){
            abilityIdx[k]   = zeros.get(k);   // 0:폭탄 1:스탑 2:사륜안
            abilityUsed[k]  = false;
        }

        invalidate();
    }

}
