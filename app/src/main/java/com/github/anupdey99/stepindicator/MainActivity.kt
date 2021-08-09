package com.github.anupdey99.stepindicator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.github.anupdey99.stepindicator.adapter.ViewPagerAdapter
import com.github.anupdey99.stepindicator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dataAdapter = ViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageList: MutableList<String> = MutableList(5) { index -> "$index+1" }
        dataAdapter.loadInitData(pageList)

        with(binding.viewPager) {
            adapter = dataAdapter
            offscreenPageLimit = 1
        }

        with(binding.stepIndicator) {
            this.isClickable = false
            this.setStepsCount(pageList.size)
        }

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.stepIndicator.setCurrentStepPosition(position)
            }
        })

    }
}