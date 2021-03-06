package com.stephen.test.mvvm.framework.activitys

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.android.puy.mvvm.base.XFragmentationActivity
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.activitys.viewmodel.IpViewModel
import com.stephen.test.mvvm.framework.databinding.ActivityIpViewBinding
import com.stephen.test.mvvm.framework.fragments.PageOne
import com.stephen.test.mvvm.framework.fragments.PageTwo
import com.stephen.test.mvvm.framework.utils.singleClick
import kotlinx.android.synthetic.main.activity_ip_view.*

class IpViewActivity : XFragmentationActivity<IpViewModel, ActivityIpViewBinding>() {

    override fun initViewMode(): IpViewModel = getViewModel(IpViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.activity_ip_view

    override fun initView(savedInstanceState: Bundle?) {
        initStatusBar(R.id.titleFy, statusBarDark = false)

        backImgV.singleClick {
            backBtnClickResponse()
        }

        val list = ArrayList<Fragment>()
        list.add(PageOne())
        list.add(PageTwo())
        viewPager.adapter = FragmentPager(supportFragmentManager, list)
    }

    override fun initData() {
        with(mViewModel) {
            mBinding?.curViewModel = this
            initData()
        }
    }

    private class FragmentPager(fm: FragmentManager, var list: java.util.ArrayList<Fragment>) : FragmentPagerAdapter(fm) {
        override fun getItem(arg0: Int): Fragment {
            return list[arg0]
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}