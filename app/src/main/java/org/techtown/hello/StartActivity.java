package org.techtown.hello;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* ① 레이아웃: 버튼 하나만 중앙에 */
        Button btn = new Button(this);
        btn.setText("Game Start");
        btn.setAllCaps(false);
        btn.setTextSize(24);

        /* ② 클릭 → MainActivity 로 전환 */
        btn.setOnClickListener(v -> {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
        });

        /* ③ 버튼을 match_parent로 감싸 중앙 배치 */
        setContentView(btn,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
