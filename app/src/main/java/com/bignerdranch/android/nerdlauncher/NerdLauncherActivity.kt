package com.bignerdranch.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NerdLauncherActivities"

class NerdLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_nerd_launcher)
        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        }
        Log.i(TAG, "Found ${activities.size} activities")
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, SearchView.OnQueryTextListener {
        private lateinit var resolveInfo: ResolveInfo
        private val nameTextView: TextView = itemView.findViewById(R.id.app_name)
        private var appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        private var searchApp: SearchView = itemView.findViewById(R.id.search_apps)
        init {
            nameTextView.setOnClickListener(this)
            appIcon.setOnClickListener(this)
            searchApp.setOnQueryTextListener(this)
            searchApp.isSubmitButtonEnabled = true
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appsIcon = resolveInfo.loadIcon(packageManager)
            nameTextView.text = appName
            appIcon.setImageDrawable(appsIcon)
        }

        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = view.context
            context.startActivity(intent)
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            if (query != null) {
                searchApps(query)
            }
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (query != null) {
                searchApps(query)
            }
            return true
        }

        private fun searchApps(query: String) {
            val searchQuery = "%$query%"
            // Still working
        }
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>() {
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater.inflate(R.layout.fragment_nerd_launcher, container, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}