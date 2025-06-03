package com.example.planty

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planty.data.CommunityPost
import com.example.planty.databinding.ActivityCommunityDetailBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView

class CommunityDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityDetailBinding
    private val commentList = mutableListOf<String>()
    private lateinit var commentAdapter: CommentAdapter
    private var liked = false
    private var likeCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정 및 뒤로가기
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 더미 데이터 (실제 앱에서는 post_id로 서버에서 받아옴)
        val post = CommunityPost(1, "처음 열매 나왔어요!", "혼자 잘 자라네요 ㅎㅎ", R.drawable.tlranf, 3, 2)
        binding.ivDetailImage.setImageResource(post.imageResId)
        binding.ivDetailImage.apply {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        binding.tvDetailTitle.text = post.title
        binding.tvDetailContent.text = post.content
        binding.tvDetailAuthor.text = "haha (닉네임)"
        likeCount = post.likeCount
        binding.tvDetailLike.text = likeCount.toString()
        binding.tvDetailComment.text = post.commentCount.toString()

        // 좋아요 토글
        binding.ivLike.setOnClickListener {
            liked = !liked
            if (liked) {
                likeCount++
                binding.ivLike.setImageResource(R.drawable.ic_favorite)
            } else {
                likeCount--
                binding.ivLike.setImageResource(R.drawable.ic_favorite_border)
            }
            binding.tvDetailLike.text = likeCount.toString()
        }

        // 댓글 더미
        commentList.addAll(listOf("예쁘네요~", "제 토토도 얼른 자라면 좋겠어요"))
        commentAdapter = CommentAdapter(commentList)
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.adapter = commentAdapter

        // 댓글 등록 기능
        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                commentList.add(text)
                commentAdapter.notifyItemInserted(commentList.size - 1)
                binding.etComment.text.clear()
                binding.rvComments.scrollToPosition(commentList.size - 1)
                // 댓글 수 갱신
                binding.tvDetailComment.text = commentList.size.toString()
            } else {
                Toast.makeText(this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// 댓글 어댑터 (간단 버전)
class CommentAdapter(private val comments: List<String>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvComment.text = comments[position]
    }
    override fun getItemCount(): Int = comments.size
} 