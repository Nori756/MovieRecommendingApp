package com.example.movierecommendingapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var mToolbar: Toolbar
    private var mGenre = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            // ログインしていなければログイン画面に遷移させる
            if (user == null) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
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
        return true
    }
}