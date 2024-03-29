package com.example.movierecommendingapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.util.Base64
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.list_question_detail.*
import java.util.*
import java.util.Arrays.sort
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var mToolbar: Toolbar
    private var mGenre = 0

    private lateinit var mDatabaseReference:DatabaseReference

    private lateinit var mListView: ListView

    private lateinit var mMoviesArrayList: ArrayList<Movie>

    private lateinit var mAdapter: MoviesListAdapter

    private var mGenreRef: DatabaseReference? = null


    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val title = map["title"] ?: ""
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""
            val imageString = map["image"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }


            var total = 0

            val commentArrayList = ArrayList<Comment>()
            val commentMap = map["comments"] as Map<String, String>?
            if (commentMap != null) {
                for (key in commentMap.keys) {
                    val temp = commentMap[key] as Map<String, String>
                    val commentBody = temp["body"] ?: ""
                    val commentName = temp["name"] ?: ""

                    val uid = temp["uid"] ?: ""


                    val grade = temp["grade"] ?: ""

                    val comment = Comment(commentBody, commentName, uid,key,grade)
                    commentArrayList.add(comment)

                 total += grade.toInt()

                }
            }




            val movie = Movie(title, body, name, uid, dataSnapshot.key ?: "",
                mGenre, bytes, commentArrayList,total)

            mMoviesArrayList.add(movie)

            val sortedList = mMoviesArrayList.sortedWith(compareBy({ it.total }))


            mMoviesArrayList = ArrayList<>.sortedList

            mAdapter.notifyDataSetChanged()
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            // 変更があったQuestionを探す
            for (movie in mMoviesArrayList) {
                if (dataSnapshot.key.equals(movie.questionUid)) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    movie.comments.clear()
                    val movieMap = map["comments"] as Map<String, String>?
                    if (movieMap != null) {
                        for (key in movieMap.keys) {
                            val temp = movieMap[key] as Map<String, String>
                            val commentBody = temp["body"] ?: ""
                            val commentName = temp["name"] ?: ""
                            val uid = temp["uid"] ?: ""

                            val grade = temp["grade"] ?: ""

                            val comment = Comment(commentBody, commentName, uid,key,grade)
                            movie.comments.add(comment)
                        }
                    }

                    mAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        fab.setOnClickListener { view ->
            // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
            if (mGenre == 0) {
                Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show()
            } else {

            }
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // ジャンルを渡して質問作成画面を起動する
                val intent = Intent(applicationContext, MovieSendActivity::class.java)
                intent.putExtra("genre", mGenre)
                startActivity(intent)
            }
        }

        // ナビゲーションドロワーの設定
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = MoviesListAdapter(this)
        mMoviesArrayList = ArrayList<Movie>()

        mAdapter.notifyDataSetChanged()


        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, MovieDetailActivity::class.java)
            intent.putExtra("movie", mMoviesArrayList[position])
            startActivity(intent)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_action) {
            mToolbar.title = "アクション"
            mGenre = 1
        } else if (id == R.id.nav_drama) {
            mToolbar.title = "ドラマ"
            mGenre = 2
        } else if (id == R.id.nav_comedy) {
            mToolbar.title = "コメディ"
            mGenre = 3
        } else if (id == R.id.nav_horror) {
            mToolbar.title = "ホラー"
            mGenre = 4
        } else if (id == R.id.nav_history) {
            mToolbar.title = "歴史物"
            mGenre = 5
        } else if (id == R.id.nav_documentary) {
            mToolbar.title = "ドキュメンタリー"
            mGenre = 6
        } else if (id == R.id.nav_mystery) {
            mToolbar.title = "ミステリー"
            mGenre = 7
        } else if (id == R.id.nav_sci_fi) {
            mToolbar.title = "SF"
            mGenre = 8
        } else if (id == R.id.nav_animation) {
            mToolbar.title = "アニメ"
            mGenre = 9
        } else if (id == R.id.nav_kids) {
            mToolbar.title = "子供向け"
            mGenre = 10
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        mMoviesArrayList.clear()
        mAdapter.setMovieArrayList(mMoviesArrayList)
        mListView.adapter = mAdapter

        // 選択したジャンルにリスナーを登録する
        if (mGenreRef != null) {
            mGenreRef!!.removeEventListener(mEventListener)
        }
        mGenreRef = mDatabaseReference.child(ContentsPATH).child(mGenre.toString())
        mGenreRef!!.addChildEventListener(mEventListener)




        return true
    }
}