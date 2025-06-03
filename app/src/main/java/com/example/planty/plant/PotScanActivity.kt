package com.example.planty.plant

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.planty.MainActivity
import com.example.planty.databinding.ActivityPotScanBinding
import android.graphics.Color
import android.widget.TextView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter

class PotScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPotScanBinding
    private var nickname: String = "토토"
    private var rotateAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPotScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nickname = intent.getStringExtra("plant_nickname") ?: "토토"

        // 모든 안내, 버튼, 애니메이션 초기 상태
        binding.tvScanGuide.text = ""
        binding.tvScanGuide2.text = ""
        binding.tvScanGuide.visibility = View.INVISIBLE
        binding.tvScanGuide2.visibility = View.INVISIBLE
        binding.btnScan.visibility = View.INVISIBLE
        binding.lottieScan.visibility = View.INVISIBLE
        binding.lottieCheck.visibility = View.INVISIBLE

        // 애니메이션 함수 정의
        fun animateTextIn(tv: TextView, text: String, onEnd: (() -> Unit)? = null) {
            tv.text = text
            tv.alpha = 0f
            tv.translationY = 40f // 아래에서 시작
            tv.visibility = View.VISIBLE
            tv.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .withEndAction {
                    onEnd?.invoke()
                }
                .start()
        }

        // 첫 안내
        animateTextIn(binding.tvScanGuide, "Planty mini가 ${nickname}를 스캔할 준비가 되었어요") {
            Handler(Looper.getMainLooper()).postDelayed({
                // 두 번째 안내
                animateTextIn(binding.tvScanGuide2, "배드의 중심에 토토를 올려주세요") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        // 버튼 등장
                        binding.btnScan.alpha = 0f
                        binding.btnScan.visibility = View.VISIBLE
                        binding.btnScan.animate()
                            .alpha(1f)
                            .setDuration(600)
                            .start()
                        // Lottie 애니메이션은 이제 startScan()에서만 실행
                    }, 2000)
                }
            }, 2000)
        }

        binding.btnScan.setOnClickListener {
            startScan()
        }

        // Lottie scan 애니메이션 끝에 체크 애니메이션 실행
        binding.lottieScan.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.lottieScan.visibility = View.GONE
                binding.lottieCheck.visibility = View.VISIBLE
                binding.lottieCheck.playAnimation()
            }
        })
        // 체크 애니메이션 끝에 안내 및 이동
        binding.lottieCheck.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                showScanCompleteText("스캔이 완료되었어요! 이제 ${nickname}을(를) 관리할 준비가 됐어요.")
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@PotScanActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }, 2000)
            }
        })
    }

    // 스캔 완료 안내만 보이게
    private fun showScanCompleteText(text: String) {
        binding.tvScanGuide.visibility = View.GONE
        binding.tvScanGuide2.visibility = View.GONE
        binding.btnScan.visibility = View.GONE
        binding.lottieScan.visibility = View.GONE
        binding.tvScanGuide.text = text
        binding.tvScanGuide.visibility = View.VISIBLE
    }

    private fun startScan() {
        binding.btnScan.isEnabled = false
        binding.btnScan.visibility = View.INVISIBLE
        // Lottie 애니메이션 보이기 및 15초 동안 1회 재생
        binding.lottieScan.cancelAnimation()
        binding.lottieScan.visibility = View.VISIBLE
        binding.lottieScan.repeatCount = 0
        binding.lottieCheck.visibility = View.INVISIBLE
        binding.lottieScan.addLottieOnCompositionLoadedListener { composition ->
            val lottieDuration = composition.duration
            val speed = lottieDuration / 15000f
            binding.lottieScan.speed = speed
            binding.lottieScan.playAnimation()
        }
        // 더 이상 Handler로 15초 후 안내/이동 처리하지 않음. (체크 애니메이션에서 처리)
    }
} 