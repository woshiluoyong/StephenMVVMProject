package com.qeeyou.accelerator.overseas.overwall.fragments

import android.os.Bundle
import com.android.puy.mvvm.base.XFragmentation
import com.qeeyou.accelerator.overseas.overwall.R
import com.qeeyou.accelerator.overseas.overwall.databinding.FragmentPageOneBinding
import com.qeeyou.accelerator.overseas.overwall.fragments.viewmodel.PageOneViewModel

class PageOne :XFragmentation<PageOneViewModel, FragmentPageOneBinding>() {

    override fun getLayoutId(): Int  = R.layout.fragment_page_one

    override fun initViewMode() = getViewModel(PageOneViewModel::class.java)

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initData() {}
}