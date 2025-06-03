package com.example.planty

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planty.adapter.CommunityPreviewAdapter
import com.example.planty.data.CommunityPost
import com.example.planty.databinding.ActivityCommunityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding
    private lateinit var adapter: CommunityPreviewAdapter
    private val postList = mutableListOf<CommunityPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 더미 데이터 (실제 앱에서는 서버에서 받아옴)
        postList.addAll(
            listOf(
                CommunityPost(1, "처음 열매 나왔어요!", "혼자 잘 자라네요 ㅎㅎ", R.drawable.tlranf, 3, 2),
                CommunityPost(2, "영양제 같이 사실분!", "방울토마토 전용 무럭무럭", R.drawable.img_6, 2, 1),
                CommunityPost(3, "새 화분 샀어요", "분갈이 성공!", R.drawable.img_5, 1, 0),
                CommunityPost(4, "책상 위에 새 친구", "귀여운 화분을 들였어요!", R.drawable.img_1, 5, 3),
                CommunityPost(5, "창가 몬스테라", "햇빛 잘 받는 곳에 두니 더 잘 자라요.", R.drawable.img_2, 7, 4),
                CommunityPost(6, "피페로미아 성장기", "잎이 점점 커져요!", R.drawable.img_3, 4, 2),
                CommunityPost(7, "오늘의 식물 일기", "매일 조금씩 변하는 모습이 신기해요.", R.drawable.img_4, 6, 5),
                CommunityPost(8, "새로운 분갈이 도전!", "처음 해봤는데 성공적이었어요.", R.drawable.img_5, 3, 1)
            )
        )
        adapter = CommunityPreviewAdapter(postList) { post ->
            val intent = Intent(this, CommunityDetailActivity::class.java)
            intent.putExtra("post_id", post.id)
            startActivity(intent)
        }
        binding.rvCommunityAll.layoutManager = LinearLayoutManager(this)
        binding.rvCommunityAll.adapter = adapter

        binding.fabWrite.setOnClickListener {
            val intent = Intent(this, CommunityWriteActivity::class.java)
            startActivity(intent)
        }
    }
} 