package com.example.accelerometer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var view_pager: ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTabLayout()
        initToolbar()
    }

    private fun initTabLayout(){
        view_pager = findViewById(R.id.view_pager)
        tabs = findViewById(R.id.tabs)
        val sectionPagerAdapter = SectionPagerAdapter(
                this,
                supportFragmentManager
        )
        view_pager.adapter = sectionPagerAdapter
        tabs.setupWithViewPager((view_pager))
    }

    private fun initToolbar() {
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.toolbar)
            elevation = 0f
        }
        val actionBar = supportActionBar
        val toolbar = actionBar?.customView?.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)
        toolbar.contentInsetEnd
        toolbar.setPadding(0, 0, 0, 0)
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}
