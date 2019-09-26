package com.example.movierecommendingapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_comment_send.*
import kotlinx.android.synthetic.main.activity_movie_send.*
import kotlinx.android.synthetic.main.activity_movie_send.progressBar
import kotlinx.android.synthetic.main.activity_movie_send.sendButton

class CommentSendActivity: AppCompatActivity(), View.OnClickListener, DatabaseReference.CompletionListener {

        private lateinit var mMovie: Movie
    var number =0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_comment_send)

            // 渡ってきたMovieのオブジェクトを保持する
            val extras = intent.extras
            mMovie = extras.get("movie") as Movie

            // UIの準備
            sendButton.setOnClickListener(this)
        }

        override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
            progressBar.visibility = View.GONE

            if (databaseError == null) {
                finish()
            } else {
                Snackbar.make(findViewById(android.R.id.content), "投稿に失敗しました", Snackbar.LENGTH_LONG).show()
            }

        }
    override fun onResume() {
        super.onResume()


        Button1.setOnClickListener{
            Button1.setVisibility(View.INVISIBLE) // 1表示しない
            Button2.setVisibility(View.VISIBLE) // 表示
            Button3.setVisibility(View.VISIBLE) // 表示
            Button4.setVisibility(View.VISIBLE) // 表示
            Button5.setVisibility(View.VISIBLE) // 表示
            number = 1

        }
        Button2.setOnClickListener{
            Button1.setVisibility(View.VISIBLE) // 表示
            Button2.setVisibility(View.INVISIBLE) // 2表示しない
            Button3.setVisibility(View.VISIBLE) // 表示
            Button4.setVisibility(View.VISIBLE) // 表示
            Button5.setVisibility(View.VISIBLE) // 表示
            number = 2
        }

        Button3.setOnClickListener{
            Button1.setVisibility(View.VISIBLE) // 表示
            Button2.setVisibility(View.VISIBLE) // 表示
            Button3.setVisibility(View.INVISIBLE) // 3表示しない
            Button4.setVisibility(View.VISIBLE) // 表示
            Button5.setVisibility(View.VISIBLE) // 表示
            number = 3
        }

        Button4.setOnClickListener{
            Button1.setVisibility(View.VISIBLE) // 表示
            Button2.setVisibility(View.VISIBLE) // 表示
            Button3.setVisibility(View.VISIBLE) // 表示
            Button4.setVisibility(View.INVISIBLE) // 4表示しない
            Button5.setVisibility(View.VISIBLE) // 表示

            number = 4
        }

        Button5.setOnClickListener{
            Button1.setVisibility(View.VISIBLE) // 表示
            Button2.setVisibility(View.VISIBLE) // 表示
            Button3.setVisibility(View.VISIBLE) // 表示
            Button4.setVisibility(View.VISIBLE) // 表示
            Button5.setVisibility(View.INVISIBLE) // 5表示しない
            number = 5
        }




    }

        override fun onClick(v: View) {
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val dataBaseReference = FirebaseDatabase.getInstance().reference

            val commentRef = dataBaseReference.child(ContentsPATH).child(mMovie.genre.toString()).child(mMovie.questionUid).child(CommentsPATH)

            commentRef.child(ContentsPATH).removeValue()

            val data = HashMap<String, String>()

            // UID
            data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid




            // 表示名
            // Preferenceから名前を取る
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val name = sp.getString(NameKEY, "")
            data["name"] = name

            // コメントを取得する
            val comment = commentEditText.text.toString()

            if (comment.isEmpty()) {
                // コメントが入力されていない時はエラーを表示するだけ
                Snackbar.make(v, "コメントを入力して下さい", Snackbar.LENGTH_LONG).show()
                return
            }
            data["body"] = comment




            data["grade"] = number.toString()

            progressBar.visibility = View.VISIBLE
            commentRef.push().setValue(data, this)
        }

    }