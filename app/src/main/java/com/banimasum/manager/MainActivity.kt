package com.banimasum.manager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.banimasum.manager.fragments.DashboardFragment
import com.banimasum.manager.fragments.StudentListFragment
import com.banimasum.manager.fragments.ToolListFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupNavigation()
        setupEdgeToEdge()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment.newInstance())
            navigationView.setCheckedItem(R.id.nav_dashboard)
        }
    }
    
    private fun setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)
        }
    }
    
    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment.newInstance())
                    true
                }
                R.id.nav_students -> {
                    loadFragment(StudentListFragment.newInstance())
                    true
                }
                R.id.nav_tools -> {
                    loadFragment(ToolListFragment.newInstance())
                    true
                }
                R.id.nav_sessions -> {
                    // TODO: Load SessionsFragment
                    true
                }
                R.id.nav_reports -> {
                    // TODO: Load ReportsFragment
                    true
                }
                R.id.nav_settings -> {
                    // TODO: Load SettingsFragment
                    true
                }
                R.id.nav_help -> {
                    // TODO: Load HelpFragment
                    true
                }
                else -> false
            }
            
            drawerLayout.closeDrawers()
            true
        }
    }
    
    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navigationView)
        return true
    }
}