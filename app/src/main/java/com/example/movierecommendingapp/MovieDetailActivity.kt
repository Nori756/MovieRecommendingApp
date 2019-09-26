package com.example.movierecommendingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var mMovie: Movie
    private lateinit var mAdapter: MovieDetailListAdapter
    private lateinit var mCommentRef: DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val commentUid = dataSnapshot.key ?: ""

            for (comment in mMovie.comments) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (commentUid == comment.commentUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""


            val uid = map["uid"] ?: ""

            val grade = map["grade"] ?: ""

            val comment = Comment(body, name, uid, commentUid,grade)
            mMovie.comments.add(comment)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        // 渡ってきたMovieのオブジェクトを保持する
        val extras = intent.extras
        mMovie = extras.get("movie") as Movie

        title = mMovie.title

     // ListViewの準備
        mAdapter = MovieDetailListAdapter(this, mMovie)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Movieを渡してコメント作成画面を起動する
                val intent = Intent(applicationContext, CommentSendActivity::class.java)
                intent.putExtra("movie", mMovie)
                startActivity(intent)
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mCommentRef = dataBaseReference.child(ContentsPATH).child(mMovie.genre.toString()).child(mMovie.questionUid).child(CommentsPATH)
        mCommentRef.addChildEventListener(mEventListener)
    }
}