package com.example.javabits;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView textView, delayTextView, easingTextView;
    private SeekBar delaySeekBar;
    private String textToType = "Hello!";
    private int currentDelay = 390; // Initial delay value (in ms)
    private boolean isEaseOut = true; // Toggle state
    private ValueAnimator currentAnimator; // Keep track of running animations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textView = findViewById(R.id.textView);
        delaySeekBar = findViewById(R.id.delaySeekBar);
        delayTextView = findViewById(R.id.tvDelay);
        easingTextView = findViewById(R.id.easeStateCubic);

        // Initialize SeekBar
        delaySeekBar.setMax(1000);
        delaySeekBar.setProgress(currentDelay);
        delayTextView.setText(currentDelay + "ms");

        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentDelay = Math.max(50, progress); // Ensure minimum delay
                    delayTextView.setText(currentDelay + "ms");
                    restartTypingAnimation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        easingTextView.setOnClickListener(v -> {
            isEaseOut = !isEaseOut;
            easingTextView.setText(isEaseOut ? "Easing: easeOutCubic" : "Easing: easeInOutCubic");
            restartTypingAnimation();
        });

        startTypingAnimation();
    }

    private void startTypingAnimation() {
        if (currentAnimator != null) {
            currentAnimator.cancel(); // Cancel any existing animation to prevent overlap
        }

        textView.setText("");

        currentAnimator = ValueAnimator.ofInt(0, textToType.length());
        currentAnimator.setDuration(currentDelay * textToType.length());
        currentAnimator.setInterpolator(isEaseOut ? easeOutCubic() : easeInOutCubic());

        currentAnimator.addUpdateListener(valueAnimator -> {
            int index = (int) valueAnimator.getAnimatedValue();
            textView.setText(textToType.substring(0, index));
        });

        currentAnimator.start();
    }

    private void restartTypingAnimation() {
        textView.postDelayed(this::startTypingAnimation, 100); // Prevent UI flicker
    }

    private Interpolator easeOutCubic() {
        return input -> (float) (1 - Math.pow(1 - input, 3));
    }

    private Interpolator easeInOutCubic() {
        return input -> (input < 0.5) ? (float) (4 * input * input * input) :
                (float) (1 - Math.pow(-2 * input + 2, 3) / 2);
    }
}
